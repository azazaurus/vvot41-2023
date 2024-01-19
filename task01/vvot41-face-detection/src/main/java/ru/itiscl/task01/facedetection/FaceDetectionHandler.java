package ru.itiscl.task01.facedetection;

import com.fasterxml.jackson.databind.*;
import org.apache.http.client.*;
import org.apache.http.impl.client.*;
import ru.itiscl.task01.facedetection.dto.*;
import ru.itiscl.task01.facedetection.dto.yandexfunctions.*;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.*;
import software.amazon.awssdk.services.s3.*;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.sqs.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.function.*;

public class FaceDetectionHandler implements Function<byte[], String> {
	private static final String propertiesFileName = "application.properties";

	private static final URI s3EndpointUrl = URI.create("https://storage.yandexcloud.net");
	private static final Region s3Region = Region.of("ru-central1");
	private static final URI sqsEndpointUrl = URI.create("https://message-queue.api.cloud.yandex.net");
	private static final Region sqsRegion = Region.of("ru-central1");

	private final PropertiesRepository propertiesRepository;
	private final ObjectMapper objectMapper;

	public FaceDetectionHandler() {
		this.propertiesRepository = new PropertiesRepository(
			FaceDetectionHandler.class.getClassLoader(),
			propertiesFileName);
		objectMapper = new ObjectMapper()
			.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	@Override
	public String apply(byte[] triggerMessagesJson) {
		try {
			return handle(objectMapper.readValue(triggerMessagesJson, TriggerMessagesDto.class));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String handle(TriggerMessagesDto newPhotoUploadedMessages) {
		var configuration = readConfiguration();
		var awsCredentialsProvider = createAwsCredentialsProvider(configuration);

		try (
				var httpClient = HttpClientBuilder.create().build();
				var s3Client = createS3Client(awsCredentialsProvider);
				var taskQueueClient = createTaskQueueClient(objectMapper, awsCredentialsProvider, configuration)) {
			var faceDetectionClient = createFaceDetectionClient(httpClient, objectMapper, configuration);

			for (var newPhotoUploadedMessage : newPhotoUploadedMessages.messages) {
				var image = getS3Object(
					s3Client,
					newPhotoUploadedMessage.details.bucketId,
					newPhotoUploadedMessage.details.objectId);
				var faceRectangles = faceDetectionClient.getFaceRectangles(image);
				for (var faceCoordinates : faceRectangles)
					taskQueueClient.addTask(
						new FacePhotoCreationTaskDto(
							newPhotoUploadedMessage.details.bucketId,
							newPhotoUploadedMessage.details.objectId,
							faceCoordinates));
			}

			return "";
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Configuration readConfiguration() {
		var properties = propertiesRepository.read();
		return new Configuration(
			properties.getProperty("yc.folder-id"),
			properties.getProperty("yc.access-key-id"),
			properties.getProperty("yc.secret-access-key"),
			properties.getProperty("yc.api-key"),
			properties.getProperty("yc.task-queue-url"));
	}

	private static byte[] getS3Object(S3Client s3Client, String bucketId, String objectId) {
		try {
			var getObjectRequest = GetObjectRequest.builder()
				.bucket(bucketId)
				.key(objectId)
				.build();
			return s3Client.getObject(getObjectRequest).readAllBytes();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static AwsCredentialsProvider createAwsCredentialsProvider(Configuration configuration) {
		return StaticCredentialsProvider.create(
			AwsBasicCredentials.create(
				configuration.staticAccessKeyId,
				configuration.secretStaticAccessKey));
	}

	private static S3Client createS3Client(AwsCredentialsProvider awsCredentialsProvider) {
		return S3Client.builder()
			.endpointOverride(s3EndpointUrl)
			.region(s3Region)
			.credentialsProvider(awsCredentialsProvider)
			.build();
	}

	private static TaskQueueClient createTaskQueueClient(
			ObjectMapper objectMapper,
			AwsCredentialsProvider awsCredentialsProvider,
			Configuration configuration) {
		return new TaskQueueClient(
			objectMapper,
			SqsClient.builder()
				.endpointOverride(sqsEndpointUrl)
				.region(sqsRegion)
				.credentialsProvider(awsCredentialsProvider)
				.build(),
			configuration);
	}

	private static FaceDetectionClient createFaceDetectionClient(
		HttpClient httpClient,
		ObjectMapper objectMapper,
		Configuration configuration) {
		return new FaceDetectionClient(
			Base64.getEncoder(),
			httpClient,
			objectMapper,
			configuration);
	}
}

