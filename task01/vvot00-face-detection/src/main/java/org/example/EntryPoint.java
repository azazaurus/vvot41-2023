package org.example;

import org.example.dto.NewPhotoUploadedEvents;
import yandex.cloud.sdk.functions.*;

public class EntryPoint implements YcFunction<NewPhotoUploadedEvents, Integer> {
    @Override
    public Integer handle(NewPhotoUploadedEvents newPhotoUploadedEvents, Context context) {
        return null;
    }
}

