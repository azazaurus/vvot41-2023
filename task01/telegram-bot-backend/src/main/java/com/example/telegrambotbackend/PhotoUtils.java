package com.example.telegrambotbackend;

import org.apache.commons.lang3.NotImplementedException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

@Component
public class PhotoUtils {
    FaceBucketClient faceBucketClient = new FaceBucketClient();
    GeneralPhotoBucketClient generalPhotoBucketClient = new GeneralPhotoBucketClient();

    @Value("${DATABASE_CONNECTION_STRING}")
    private String databaseConnectionString;
    public void init() {
        faceBucketClient.init();
        generalPhotoBucketClient.init();
    }
    public InputStream getPhotoToRecognize() {


        try {
            return new URL("https://images.unsplash.com/photo-1527960669566-f882ba85a4c6?q=80&w=1000&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8YXdlc29tZSUyMHBpY3xlbnwwfHwwfHx8MA%3D%3D")
                    .openConnection()
                    .getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<InputStream> getPhotosAssociatedWithName(String name) {
        throw new NotImplementedException();
    }
}
