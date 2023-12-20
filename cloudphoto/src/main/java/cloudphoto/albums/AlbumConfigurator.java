package cloudphoto.albums;

import cloudphoto.albums.repositories.*;
import cloudphoto.configuration.settings.*;
import cloudphoto.filesystem.*;
import cloudphoto.logs.*;
import cloudphoto.s3.*;
import org.springframework.context.annotation.*;

import java.util.*;
import java.util.function.*;

@Configuration
public class AlbumConfigurator {
	@Bean
	public S3ObjectKeyEncoder s3ObjectKeyEncoder() {
		return new Base64S3ObjectKeyEncoder(Base64.getUrlEncoder(), Base64.getUrlDecoder());
	}

	@Bean
	public AlbumRepository albumRepository(
			S3ObjectKeyEncoder s3ObjectKeyEncoder,
			Supplier<S3Settings> s3SettingsProvider,
			S3Client s3Client) {
		return new S3BucketAlbumRepository(s3ObjectKeyEncoder, s3SettingsProvider, s3Client);
	}

	@Bean
	public AlbumService albumService(
			FileService fileService,
			AlbumRepository albumRepository,
			Log log) {
		return new AlbumService(fileService, albumRepository, log);
	}
}
