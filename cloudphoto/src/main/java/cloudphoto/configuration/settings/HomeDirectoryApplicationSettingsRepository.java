package cloudphoto.configuration.settings;

import cloudphoto.common.valueerrorresult.*;
import org.apache.commons.configuration2.*;
import org.apache.commons.configuration2.ex.*;

import java.io.*;

public class HomeDirectoryApplicationSettingsRepository implements ApplicationSettingsRepository {
	private static final String settingsFileName = System.getProperty("user.home")
		+ File.separator
		+ ".config"
		+ File.separator
		+ "cloudphoto"
		+ File.separator
		+ "cloudphotorc";

	private final ApplicationSettingsConverter applicationSettingsConverter;

	public HomeDirectoryApplicationSettingsRepository(ApplicationSettingsConverter applicationSettingsConverter) {
		this.applicationSettingsConverter = applicationSettingsConverter;
	}

	@Override
	public Result<S3Settings, String> readS3Settings() {
		var iniConfiguration = new INIConfiguration();
		try (var iniFileReader = new FileReader(settingsFileName)) {
			iniConfiguration.read(iniFileReader);
		} catch (IOException | ConfigurationException e) {
			return Result.fail("Can't read application settings file. " + e.getMessage());
		}

		var settingsResult = applicationSettingsConverter.toSettings(iniConfiguration);
		if (settingsResult.isFailure())
			return Result.fail("Can't read application settings. " + settingsResult.getError());

		return settingsResult;
	}
}
