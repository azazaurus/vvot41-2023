package com.example.telegrambotbackend;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.beans.factory.annotation.Value;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FaceBucketClient {
    @Value("${FACES_BUCKET_NAME}")
    private String bucketName;

    @Value("${ACCESS_KEY_ID}")
    private String accessKeyId;

    @Value("${SECRET_KEY}")
    private String secretAccessKey;

    AmazonS3 s3Client;

    public void init() {
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
    }

    public InputStream get() {
        return InputStream.nullInputStream();
    }

    private String generateUniqueName() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
