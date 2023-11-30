package org.example;

import org.example.dto.FaceDetectionResultDto;
import org.example.dto.RectangleDto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CoordinatesUtils {
    public static List<Coordinates> getCoordinates(List<FaceDetectionResultDto> response) {
        return response.stream()
            .flatMap(x -> x.faceDetection.faces.stream())
            .map(face -> toCoordinates(face.boundingBox))
            .toList();
    }

    private static Coordinates toCoordinates(RectangleDto rectangle) {
        return new Coordinates(
            Integer.parseInt(rectangle.vertices.get(0).x),
            Integer.parseInt(rectangle.vertices.get(0).y),
            Integer.parseInt(rectangle.vertices.get(2).x),
            Integer.parseInt(rectangle.vertices.get(2).y));
    }
}
