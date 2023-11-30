package com.example.telegrambotbackend;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import java.io.File;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.InputStream;
import java.util.ArrayList;

@Component
public class Bot extends TelegramLongPollingBot {
    private final TelegramBotsApi telegramBotsApi;
    private final PhotoUtils photoUtils = new PhotoUtils();

    {
        try {
            telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Getter
    @Value("${bot.token}")
    private String botToken;

    @Value("${bot.name}")
    private String botName;

    @PostConstruct
    private void init() {
        try {
            telegramBotsApi.registerBot(this);
            photoUtils.init();
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void BotComponent() throws TelegramApiException {}

    @Override
    public void onUpdateReceived(Update update) {
        String message = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();
        System.out.println(message);

        SendMessage responseMessage = new SendMessage();
        SendPhoto responsePhoto = new SendPhoto();
        InputFile inputFile = new InputFile();
        if (message.matches("^/find\\s.*$")) {
            String name = message.replace("/find ", "s.jpg");
            responsePhoto.setChatId(chatId);
            ArrayList<InputStream> photos = photoUtils.getPhotosAssociatedWithName(name);
            for (InputStream photo : photos) {
                inputFile.setMedia(photo, "d");
                responsePhoto.setPhoto(inputFile);
                try {
                    execute(responsePhoto);
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        else if (message.matches("^/getface$")) {
            responsePhoto.setChatId(chatId);
            inputFile.setMedia(photoUtils.getPhotoToRecognize(), "s.jpg");
            responsePhoto.setPhoto(inputFile);
            try {
                execute(responsePhoto);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }

        else {
            responseMessage.setChatId(chatId);
            responseMessage.setText("Ошибка");
            try {
                execute(responseMessage);
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public String getBotUsername() {
        return botName;
    }
}