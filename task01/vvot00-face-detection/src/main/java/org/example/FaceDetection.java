package org.example;

import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;


public class FaceDetection {

    private final String folderId = PropertyUtil.getProperty("FOLDER_ID");

    public List<Coordinates> getFaceRectanglecoordinates(URL url) {
        byte[] photo;

        try {
            InputStream inputStream = url
                    .openConnection()
                    .getInputStream();

            photo = inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        byte[] encodedPhoto = Base64.encodeBase64(photo);

        String requestJson = "{\n" +
                "    \"folderId\": \"" + folderId + "\",\n" +
                "    \"analyze_specs\": [{\n" +
                "        \"content\": \"" + new String(encodedPhoto, StandardCharsets.UTF_8) + "\",\n" +
                "        \"features\": [{\n" +
                "            \"type\": \"FACE_DETECTION\"\n" +
                "        }]\n" +
                "    }]\n" +
                "}";

        var results = RequestUtil.send(requestJson);

        return CoordinatesUtils.getCoordinates(results);
    }

}
