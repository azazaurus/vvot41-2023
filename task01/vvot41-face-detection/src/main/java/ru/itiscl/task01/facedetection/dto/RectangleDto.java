package ru.itiscl.task01.facedetection.dto;

public class RectangleDto {
	public CoordinateDto leftTop;
	public CoordinateDto leftBottom;
	public CoordinateDto rightTop;
	public CoordinateDto rightBottom;

	public RectangleDto() {
	}

	public RectangleDto(
			CoordinateDto leftTop,
			CoordinateDto leftBottom,
			CoordinateDto rightTop,
			CoordinateDto rightBottom) {
		this.leftTop = leftTop;
		this.leftBottom = leftBottom;
		this.rightTop = rightTop;
		this.rightBottom = rightBottom;
	}
}
