package ru.itiscl.task01.telegrambotbackend;

import org.telegram.telegrambots.bots.*;
import org.telegram.telegrambots.meta.api.methods.*;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.*;

import java.io.InputStream;
import java.util.ArrayList;

public class Bot extends TelegramWebhookBot {
	private final BotConfiguration botConfiguration;

	public Bot(DefaultBotOptions options, BotConfiguration botConfiguration) {
		super(options, botConfiguration.token);

		this.botConfiguration = botConfiguration;
	}

	@Override
	public String getBotUsername() {
		return botConfiguration.username;
	}

	@Override
	public String getBotPath() {
		return "";
	}

	@Override
	public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
		var message = update.getMessage();
		var messageText = message.getText();
		var chatId = message.getChatId();

		if (messageText.matches("^/find\\s.*$")) {
			String name = messageText.replace("/find ", "s.jpg");
			findNameOnPhotos(name, chatId);
			return null;
		}

		if (messageText.matches("^/getface$")) {
			sendFace(chatId);
			return null;
		}

		sendError(chatId);
		return null;
	}

	private void findNameOnPhotos(String name, Long chatId) {
		ArrayList<InputStream> photos = new ArrayList<>();
		for (InputStream photo : photos) {
			var inputFile = new InputFile();
			inputFile.setMedia(photo, "d");

			var responsePhoto = new SendPhoto();
			responsePhoto.setChatId(chatId);
			responsePhoto.setPhoto(inputFile);
			safeExecute(responsePhoto);
		}
	}

	private void sendFace(Long chatId) {
		var inputFile = new InputFile();
		inputFile.setMedia(InputStream.nullInputStream(), "s.jpg");

		var responsePhoto = new SendPhoto();
		responsePhoto.setChatId(chatId);
		responsePhoto.setPhoto(inputFile);
		safeExecute(responsePhoto);
	}

	private void sendError(Long chatId) {
		var responseMessage = new SendMessage();
		responseMessage.setChatId(chatId);
		responseMessage.setText("Ошибка");
		safeExecute(responseMessage);
	}

	private void safeExecute(SendPhoto responsePhoto) {
		try {
			execute(responsePhoto);
		} catch (TelegramApiException e) {
			throw new RuntimeException(e);
		}
	}

	private void safeExecute(SendMessage responseMessage) {
		try {
			execute(responseMessage);
		} catch (TelegramApiException e) {
			throw new RuntimeException(e);
		}
	}
}

