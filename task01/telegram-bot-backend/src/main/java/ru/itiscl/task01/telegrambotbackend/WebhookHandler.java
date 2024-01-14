package ru.itiscl.task01.telegrambotbackend;

import com.fasterxml.jackson.databind.*;
import org.telegram.telegrambots.bots.*;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.*;
import yandex.cloud.sdk.functions.*;

import java.io.*;
import java.util.*;

public class WebhookHandler implements YcFunction<byte[], byte[]> {
	private static final String configurationFileName = "application.properties";

	private final ConfigurationRepository configurationRepository;

	public WebhookHandler() {
		configurationRepository = new ConfigurationRepository(configurationFileName);
	}

	@Override
	public byte[] handle(byte[] updateJson, Context context) {
		var bot = initializeBot();
		var update = parseUpdate(updateJson);
		bot.onWebhookUpdateReceived(update);
		return new byte[0];
	}

	private Bot initializeBot() {
		var configuration = configurationRepository.read();
		var botConfiguration = getBotConfiguration(configuration);
		return new Bot(
			new DefaultBotOptions(),
			botConfiguration);
	}

	private static BotConfiguration getBotConfiguration(Properties configuration) {
		var botConfiguration = new BotConfiguration();
		botConfiguration.username = configuration.getProperty("bot.username");
		botConfiguration.token = configuration.getProperty("bot.token");
		return botConfiguration;
	}

	private static Update parseUpdate(byte[] updateJson) {
		try {
			return new ObjectMapper().readValue(updateJson, Update.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
