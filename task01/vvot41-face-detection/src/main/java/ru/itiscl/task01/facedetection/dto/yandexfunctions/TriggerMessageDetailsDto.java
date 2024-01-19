package ru.itiscl.task01.facedetection.dto.yandexfunctions;

import com.fasterxml.jackson.annotation.*;

public class TriggerMessageDetailsDto {
	@JsonProperty("bucket_id")
	public String bucketId;

	@JsonProperty("object_id")
	public String objectId;

	public TriggerMessageDetailsDto() {
	}

	public TriggerMessageDetailsDto(String bucketId, String objectId) {
		this.bucketId = bucketId;
		this.objectId = objectId;
	}
}
