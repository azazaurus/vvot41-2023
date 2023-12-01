package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.example.dto.FaceDetectionResultResultResultsDto;

import java.io.IOException;

public class TasksQueueUtil {
    public void addTask(String objectId, String bucketId, Coordinates faceCoordinates) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        String json = getJson(objectId, bucketId, faceCoordinates);
        HttpPost httpPost = new HttpPost("Action=SendMessage\n" +
            "&Version=2012-11-05\n" +
            "&QueueUrl=https://message-queue.api.cloud.yandex.net/b1g8ad42m6he1ooql78r/dj600000000000le07ol/sample-queue\n" +
            "&MessageBody=");

        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);

        httpPost.setHeader("Host", "message-queue.api.cloud.yandex.net");
        httpPost.setHeader("Content-Length", "length");
        httpPost.setHeader("Content-type", "application/x-www-form-urlencoded");
        httpPost.setHeader("Authorization", PropertyUtil.getProperty("AUTHORIZATION"));

        HttpResponse response;
        try {
            response = httpClient.execute(httpPost);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        var mapper = new ObjectMapper();
        try {
            var result = mapper.readValue(response.getEntity().getContent(), FaceDetectionResultResultResultsDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getJson(String objectId, String bucketId, Coordinates faceCoordinates) {
        String json = "{" +
            "\"objectId\": \""+ objectId +"\", " +
            "\"bucketId\": \"" + bucketId + "\", " +
            "\"coordinates\": " +
                "{\"leftTop\": " +
                        "{\"x\": "+ faceCoordinates.leftTop[0] +", " +
                        "\"y\": " + faceCoordinates.leftTop[1]  + "}, " +
                "\"leftBottom\": {\"x\": "+ faceCoordinates.leftBottom[0] +", " +
                                "\"y\": "+ faceCoordinates.leftBottom[1] +"}, " +
                "\"rightTop\": {\"x\": "+ faceCoordinates.rightTop[0] +", " +
                                "\"y\": "+ faceCoordinates.rightTop[1] +"}, " +
                "\"rightBottom\": {\"x\": "+ faceCoordinates.rightBottom[0] +", " +
                                    "\"y\": "+ faceCoordinates.rightBottom[0] +"}}}";

        return json;
    }
}
