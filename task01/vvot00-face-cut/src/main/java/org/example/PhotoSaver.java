package org.example;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class PhotoSaver {
    private final String bucketName = PropertyUtil.getProperty("FACES_BUCKET_NAME");

    private final String accessKeyId = PropertyUtil.getProperty("ACCESS_KEY_ID");

    private final String secretAccessKey = PropertyUtil.getProperty("SECRET_KEY");

    public List<String> save(List<byte[]> photos) {
        List<String> urls = new ArrayList<>();

        final AmazonS3 s3Client;

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

        try {
            // Создание пула потоков
            ExecutorService executorService = Executors.newFixedThreadPool(photos.size());
            List<Future<String>> futures = new ArrayList<>();

            for (byte[] photoBytes : photos) {
                Future<String> future = executorService.submit(() -> {
                    String fileName = generateUniqueName();
                    ObjectMetadata metadata = new ObjectMetadata();
                    metadata.setContentLength(photoBytes.length);

                    // Загрузка файла в Yandex Object Storage
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(photoBytes);
                    s3Client.putObject(bucketName, fileName, inputStream, metadata);
                    System.out.println("Upload Service. Added file: " + fileName + " to bucket: " + bucketName);

                    // Получение ссылки на загруженный файл
                    String url = s3Client.getUrl(bucketName, fileName).toExternalForm();

                    return url;
                });

                futures.add(future);
            }

            // Ожидание завершения задач
            for (Future<String> future : futures) {
                try {
                    String url = future.get();
                    urls.add(url);
                } catch (InterruptedException | ExecutionException e) {
                    System.out.println("One of the thread ended with exception. Reason: {}" + e.getMessage());
                    throw new RuntimeException(e);
                }
            }

            executorService.shutdown();
        } catch (AmazonS3Exception e) {
            System.out.println("Error uploading photos to Object Storage. Reason: {}" + e.getMessage());
            throw new AmazonS3Exception(e.getMessage());
        }
        return urls;
    }

    private String generateUniqueName() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
