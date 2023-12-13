package cloudphoto.configuration.settings;

import cloudphoto.common.valueerrorresult.*;

public interface ApplicationSettingsRepository {
	Result<S3Settings, String> readS3Settings();
}
