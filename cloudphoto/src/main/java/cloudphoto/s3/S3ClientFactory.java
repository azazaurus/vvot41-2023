package cloudphoto.s3;

import cloudphoto.configuration.settings.*;

import java.util.function.*;

public interface S3ClientFactory {
	S3Client create(Supplier<S3Settings> s3SettingsProvider);
}
