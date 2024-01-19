package ru.itiscl.task01.facedetection;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class PropertiesRepository {
	private final ClassLoader classLoader;
	private final String propertiesFileName;

	public PropertiesRepository(ClassLoader classLoader, String propertiesFileName) {
		this.classLoader = classLoader;
		this.propertiesFileName = propertiesFileName;
	}

	public Properties read() {
		var configuration = new Properties();

		try (var configurationFileStream = classLoader.getResourceAsStream(propertiesFileName)) {
			if (configurationFileStream != null)
				configuration.load(configurationFileStream);
		} catch (IOException ignored) {
		}

		try (var configurationFileStream = Files.newInputStream(Path.of(propertiesFileName))) {
			configuration.load(configurationFileStream);
		} catch (IOException ignored) {
		}

		return configuration;
	}
}
