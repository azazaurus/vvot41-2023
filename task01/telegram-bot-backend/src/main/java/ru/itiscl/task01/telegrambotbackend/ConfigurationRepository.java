package ru.itiscl.task01.telegrambotbackend;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ConfigurationRepository {
	private final String configurationFileName;

	public ConfigurationRepository(String configurationFileName) {
		this.configurationFileName = configurationFileName;
	}

	public Properties read() {
		try (var configurationFileStream = Files.newInputStream(Path.of(configurationFileName))) {
			var configuration = new Properties();
			configuration.load(configurationFileStream);
			return configuration;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
