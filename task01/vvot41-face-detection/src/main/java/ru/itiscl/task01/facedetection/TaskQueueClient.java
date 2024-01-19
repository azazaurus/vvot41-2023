package ru.itiscl.task01.facedetection;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.*;
import ru.itiscl.task01.facedetection.dto.*;
import software.amazon.awssdk.services.sqs.*;
import software.amazon.awssdk.services.sqs.model.*;

public class TaskQueueClient implements AutoCloseable {
	private final ObjectMapper objectMapper;
	private final SqsClient sqsClient;
	private final Configuration configuration;

	public TaskQueueClient(ObjectMapper objectMapper, SqsClient sqsClient, Configuration configuration) {
		this.objectMapper = objectMapper;
		this.sqsClient = sqsClient;
		this.configuration = configuration;
	}

	public void addTask(FacePhotoCreationTaskDto taskDto) {
		var taskJson = toJson(taskDto);
		var sendMessageRequest = SendMessageRequest.builder()
			.queueUrl(configuration.taskQueueUrl)
			.messageBody(taskJson)
			.build();
		sqsClient.sendMessage(sendMessageRequest);
	}

	@Override
	public void close() {
		sqsClient.close();
	}

	private String toJson(Object value) {
		try {
			return objectMapper.writeValueAsString(value);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}
