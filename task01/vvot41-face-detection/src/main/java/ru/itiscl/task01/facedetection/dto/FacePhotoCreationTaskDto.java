package ru.itiscl.task01.facedetection.dto;

public class FacePhotoCreationTaskDto {
	public String bucketId;
	public String objectId;
	public RectangleDto faceRectangle;

	public FacePhotoCreationTaskDto() {
	}

	public FacePhotoCreationTaskDto(String bucketId, String objectId, RectangleDto faceRectangle) {
		this.bucketId = bucketId;
		this.objectId = objectId;
		this.faceRectangle = faceRectangle;
	}
}
