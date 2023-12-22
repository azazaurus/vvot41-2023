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

	@Bean
	public S3Settings s3Settings(Result<S3Settings, String> s3SettingsResult) {
		if (s3SettingsResult.isFailure())
			throw new RuntimeException(s3SettingsResult.getError());

		return s3SettingsResult.getValue();
	}
}
