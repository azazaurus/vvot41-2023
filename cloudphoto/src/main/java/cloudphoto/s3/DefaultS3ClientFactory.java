package cloudphoto.s3;

import cloudphoto.common.*;
import cloudphoto.configuration.settings.*;
import software.amazon.awssdk.auth.credentials.*;
import software.amazon.awssdk.regions.*;

import java.util.function.*;

public class DefaultS3ClientFactory implements S3ClientFactory {
	@Override
	public DefaultS3Client create(Supplier<S3Settings> s3SettingsProvider) {
		return new DefaultS3Client(
			new Lazy<>(
				() -> {
					var s3Settings = s3SettingsProvider.get();
					return software.amazon.awssdk.services.s3.S3Client.builder()
						.endpointOverride(s3Settings.accessEndpointUrl.toURI())
						.credentialsProvider(
							StaticCredentialsProvider.create(
								AwsBasicCredentials.create(s3Settings.accessKeyId, s3Settings.secretAccessKey)))
						.region(Region.of(s3Settings.region))
						.build();
				}));
	}
}
