package com.example.telegrambotbackend;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.starter.SpringWebhookBot;

import java.io.InputStream;
import java.util.ArrayList;

@Component
public class Bot extends SpringWebhookBot {
    private final BotConfig botConfig;
    private final PhotoUtils photoUtils;

    public Bot(DefaultBotOptions options, SetWebhook setWebhook, BotConfig botConfig, PhotoUtils photoUtils) {
        super(options, setWebhook, botConfig.token);

        this.botConfig = botConfig;
        this.photoUtils = photoUtils;
    }

    @Override
    public String getBotUsername() {
        return botConfig.username;
    }

    @Override
    public String getBotPath() {
        return null;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
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

            return responseMessage;
        }

        return null;
    }
}
