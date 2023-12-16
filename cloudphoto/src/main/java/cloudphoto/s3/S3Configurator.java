package cloudphoto.s3;

import cloudphoto.configuration.settings.*;
import org.springframework.context.annotation.*;

import java.util.function.*;

@Configuration
public class S3Configurator {
	@Bean
	public S3Client s3Client(
			S3ClientFactory s3ClientFactory,
			Supplier<S3Settings> s3SettingsProvider) {
		return s3ClientFactory.create(s3SettingsProvider);
	}

	@Bean
	public S3ClientFactory s3ClientFactory() {
		return new DefaultS3ClientFactory();
	}
}
