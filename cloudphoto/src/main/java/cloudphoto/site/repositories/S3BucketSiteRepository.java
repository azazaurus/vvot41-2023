package cloudphoto.site.repositories;

import cloudphoto.common.errorresult.*;
import cloudphoto.configuration.settings.*;
import cloudphoto.s3.*;

import java.nio.charset.*;
import java.util.function.*;

public class S3BucketSiteRepository implements SiteRepository {
	private static final Charset pageCharset = StandardCharsets.UTF_8;

	private final Supplier<S3Settings> s3SettingsProvider;
	private final S3Client s3Client;

	public S3BucketSiteRepository(Supplier<S3Settings> s3SettingsProvider, S3Client s3Client) {
		this.s3SettingsProvider = s3SettingsProvider;
		this.s3Client = s3Client;
	}

	@Override
	public Result<String> uploadPage(String pagePath, String pageContent) {
		var bucketName = s3SettingsProvider.get().bucketName;
		return s3Client.uploadObject(
			bucketName,
			pagePath,
			"text/html; charset=" + pageCharset.name(),
			pageContent.getBytes(pageCharset));
	}

	@Override
	public Result<String> publishWebsite(String indexPagePath, String errorPagePath) {
		var bucketName = s3SettingsProvider.get().bucketName;
		return s3Client.publishBucketWebsite(bucketName, indexPagePath, errorPagePath);
	}
}
