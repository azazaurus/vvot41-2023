package org.example;

import yandex.cloud.sdk.functions.Context;


public class EntryPoint implements YcFunction<NewPhotoUploadedEvents, Integer> {
    @Override
    public Integer handle(NewPhotoUploadedEvents newPhotoUploadedEvents, Context context) {
        return null;
    }
}
