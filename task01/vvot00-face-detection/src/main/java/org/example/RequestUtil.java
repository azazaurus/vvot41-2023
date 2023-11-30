package org.example;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.databind.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.example.dto.*;

public class RequestUtil {
    public static List<FaceDetectionResultDto> send(String json) {
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost("https://vision.api.cloud.yandex.net/vision/v1/batchAnalyze");

        StringEntity entity = new StringEntity(json, ContentType.APPLICATION_JSON);
        httpPost.setEntity(entity);

        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-type", "application/json; charset=UTF-8");
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
            return result.results.stream()
                .flatMap(x -> x.results.stream())
                .toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
