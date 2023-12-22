package cloudphoto.configuration.settings;

import cloudphoto.common.valueerrorresult.*;

public interface ApplicationSettingsRepository {
	Result<S3Settings, String> readS3Settings();

	cloudphoto.common.errorresult.Result<String> writeS3Settings(S3Settings s3Settings);
}
