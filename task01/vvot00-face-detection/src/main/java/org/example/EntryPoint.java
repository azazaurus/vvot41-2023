package org.example;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import org.example.dto.NewPhotoUploadedEvent;
import org.example.dto.NewPhotoUploadedEvents;
import yandex.cloud.sdk.functions.Context;
import yandex.cloud.sdk.functions.YcFunction;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class EntryPoint implements YcFunction<NewPhotoUploadedEvents, Integer> {
    final String accessKeyId = PropertyUtil.getProperty("");
    final String secretAccessKey = PropertyUtil.getProperty("");

    TasksQueueUtil tasksQueueUtil = new TasksQueueUtil();

    FaceDetection faceDetection = new FaceDetection();

    @Override
    public Integer handle(NewPhotoUploadedEvents newPhotoUploadedEvents, Context context) {

        final AmazonS3 s3Client;

        try {
            // Создание клиента AmazonS3 с подключением к Object Storage
            s3Client = AmazonS3ClientBuilder.standard()
                .withEndpointConfiguration(
                    new com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration(
                        "https://storage.yandexcloud.net",
                        "ru-central1"
                    )
                )
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKeyId, secretAccessKey)))
                .build();
        } catch (SdkClientException e) {
            System.out.println("Error creating client for Object Storage via AWS SDK. Reason: {}" + e.getMessage());
            throw new SdkClientException(e.getMessage());
        }

        String bucketId;
        String objectId;
        NewPhotoUploadedEvent[] newPhotoUploadedEventsArray = newPhotoUploadedEvents.messages;
        for (NewPhotoUploadedEvent newPhotoUploadedEvent: newPhotoUploadedEventsArray) {
            bucketId = newPhotoUploadedEvent.details.bucketId;
            objectId = newPhotoUploadedEvent.details.objectId;
            InputStream inputStream;

            S3Object s3Object = s3Client.getObject(bucketId, objectId);
            try {
                inputStream = new ByteArrayInputStream(s3Object.getObjectContent().readAllBytes());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            List<Coordinates> coordinates = faceDetection.getFaceRectanglecoordinates(inputStream);
            for (Coordinates faceCoordinates: coordinates) {
                tasksQueueUtil.addTask(objectId, bucketId, faceCoordinates);
            }

        }
        return 1;
    }
}

