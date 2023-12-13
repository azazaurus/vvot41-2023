package cloudphoto.configuration.settings;


import cloudphoto.common.valueerrorresult.*;
import org.springframework.context.annotation.*;

@Configuration
public class ApplicationSettingsConfigurator {
	@Bean
	public ApplicationSettingsConverter applicationSettingsConverter() {
		return new ApplicationSettingsConverter();
	}

	@Bean
	public ApplicationSettingsRepository applicationSettingsRepository(
			ApplicationSettingsConverter applicationSettingsConverter) {
		return new HomeDirectoryApplicationSettingsRepository(applicationSettingsConverter);
	}

	@Bean
	public Result<S3Settings, String> s3SettingsResult(
			ApplicationSettingsRepository applicationSettingsRepository) {
		return applicationSettingsRepository.readS3Settings();
	}
}
