package ru.itiscl.task01.facedetection;

import com.fasterxml.jackson.databind.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.entity.*;
import ru.itiscl.task01.facedetection.dto.*;
import ru.itiscl.task01.facedetection.dto.RectangleDto;
import ru.itiscl.task01.facedetection.dto.yandexvision.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

public class FaceDetectionClient {
	private static final String faceDetectionEndpointUrl = "https://vision.api.cloud.yandex.net/vision/v1/batchAnalyze";
	private static final String imageMimeType = "image/jpeg"; // Only JPEG is supported

	private final Base64.Encoder base64Encoder;
	private final HttpClient httpClient;
	private final ObjectMapper objectMapper;
	private final Configuration configuration;

	public FaceDetectionClient(
			Base64.Encoder base64Encoder,
			HttpClient httpClient,
			ObjectMapper objectMapper,
			Configuration configuration) {
		this.base64Encoder = base64Encoder;
		this.httpClient = httpClient;
		this.objectMapper = objectMapper;
		this.configuration = configuration;
	}

	public List<RectangleDto> getFaceRectangles(byte[] image) {
		var faceDetectionRequest = createFaceDetectionRequest(image);
		var faceDetectionResponse = executeFaceDetectionRequest(faceDetectionRequest);
		return getRectangles(faceDetectionResponse);
	}

	private HttpPost createFaceDetectionRequest(byte[] image) {
		var base64EncodedImageContent = base64Encoder.encodeToString(image);
		var faceDetectionRequestBody = """
			{
				"folderId": \"""" + configuration.cloudFolderId + "\",\n"
			+ """
				"analyze_specs": [{
					"mimeType": \"""" + imageMimeType + "\",\n"
			+ """
					"content": \"""" + base64EncodedImageContent + "\",\n"
			+ """
					"features": [{ "type": "FACE_DETECTION" }]
				}]
			}""";

		HttpPost httpPost = new HttpPost(faceDetectionEndpointUrl);
		httpPost.setEntity(new StringEntity(faceDetectionRequestBody, ContentType.APPLICATION_JSON));
		httpPost.setHeader("Accept", "application/json");
		httpPost.setHeader("Content-type", "application/json; charset=UTF-8");
		httpPost.setHeader("Authorization", "Api-Key " + configuration.apiKey);
		return httpPost;
	}

	private FaceDetectionResultResultResultsDto executeFaceDetectionRequest(HttpPost request) {
		try {
			var response = httpClient.execute(request);
			return objectMapper.readValue(
				response.getEntity().getContent(),
				FaceDetectionResultResultResultsDto.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static List<RectangleDto> getRectangles(FaceDetectionResultResultResultsDto resultsDto) {
		return resultsDto.results.stream()
			.flatMap(x -> x.results.stream())
			.flatMap(x -> x.faceDetection.faces.stream())
			.map(x -> toRectangleDto(x.boundingBox))
			.collect(Collectors.toList());
	}

	private static RectangleDto toRectangleDto(
			ru.itiscl.task01.facedetection.dto.yandexvision.RectangleDto rectangle) {
		return new RectangleDto(
			toCoordinateDto(rectangle.vertices.get(0)),
			toCoordinateDto(rectangle.vertices.get(1)),
			toCoordinateDto(rectangle.vertices.get(3)),
			toCoordinateDto(rectangle.vertices.get(2)));
	}

	private static CoordinateDto toCoordinateDto(VerticeDto vertice) {
		return new CoordinateDto(
			Integer.parseInt(vertice.x),
			Integer.parseInt(vertice.y));
	}
}
