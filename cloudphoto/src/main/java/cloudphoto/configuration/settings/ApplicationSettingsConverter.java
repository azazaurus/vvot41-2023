package cloudphoto.configuration.settings;

import cloudphoto.common.valueerrorresult.*;
import org.apache.commons.configuration2.*;

import java.net.*;

public class ApplicationSettingsConverter {
	private static final String s3SettingsSectionName = "DEFAULT";

	private static final String accessEndpointUrlKey = "endpoint_url";
	private static final String accessKeyIdKey = "aws_access_key_id";
	private static final String secretAccessKeyKey = "aws_secret_access_key";
	private static final String regionKey = "region";
	private static final String bucketNameKey = "bucket";

	public Result<S3Settings, String> toSettings(INIConfiguration iniConfiguration) {
		var s3SettingsSection = iniConfiguration.getSection(s3SettingsSectionName);

		var accessEndpointUrlResult = getIniProperty(s3SettingsSection, accessEndpointUrlKey);
		if (accessEndpointUrlResult.isFailure())
			return Result.fail(accessEndpointUrlResult.getError());

		var accessKeyIdResult = getIniProperty(s3SettingsSection, accessKeyIdKey);
		if (accessKeyIdResult.isFailure())
			return Result.fail(accessKeyIdResult.getError());

		var secretAccessKeyResult = getIniProperty(s3SettingsSection, secretAccessKeyKey);
		if (secretAccessKeyResult.isFailure())
			return Result.fail(secretAccessKeyResult.getError());

		var regionResult = getIniProperty(s3SettingsSection, regionKey);
		if (regionResult.isFailure())
			return Result.fail(regionResult.getError());

		var bucketNameResult = getIniProperty(s3SettingsSection, bucketNameKey);
		if (bucketNameResult.isFailure())
			return Result.fail(bucketNameResult.getError());

		URL accessEndpointUrl;
		try {
			accessEndpointUrl = new URL(accessEndpointUrlResult.getValue());
		} catch (MalformedURLException e) {
			return Result.fail(e.getMessage());
		}

		return Result.success(
			new S3Settings(
				accessEndpointUrl,
				accessKeyIdResult.getValue(),
				secretAccessKeyResult.getValue(),
				regionResult.getValue(),
				bucketNameResult.getValue()));
	}

	public INIConfiguration toIni(S3Settings s3Settings) {
		var iniConfiguration = new INIConfiguration();
		var s3SettingsSection = iniConfiguration.getSection(s3SettingsSectionName);

		s3SettingsSection.setProperty(bucketNameKey, s3Settings.bucketName);
		s3SettingsSection.setProperty(accessKeyIdKey, s3Settings.accessKeyId);
		s3SettingsSection.setProperty(secretAccessKeyKey, s3Settings.secretAccessKey);
		s3SettingsSection.setProperty(regionKey, s3Settings.region);
		s3SettingsSection.setProperty(accessEndpointUrlKey, s3Settings.accessEndpointUrl);

		return iniConfiguration;
	}

	private static Result<String, String> getIniProperty(SubnodeConfiguration configuration, String key) {
		var propertyValue = (String)configuration.getProperty(key);
		if (propertyValue == null)
			return Result.fail("Property \"" + key + "\" is not found");

		return Result.success(propertyValue);
	}
}
