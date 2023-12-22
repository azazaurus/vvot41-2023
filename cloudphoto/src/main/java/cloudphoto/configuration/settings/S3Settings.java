package cloudphoto.configuration.settings;

import java.net.*;

public class S3Settings {
	public static final URL defaultAccessEndpointUrl;
	public static final String defaultRegion = "ru-central1";

	static {
		try {
			defaultAccessEndpointUrl = new URL("https://storage.yandexcloud.net");
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	public URL accessEndpointUrl;
	public String accessKeyId;
	public String secretAccessKey;
	public String region;
	public String bucketName;

	public S3Settings() {
	}

	public S3Settings(
			URL accessEndpointUrl,
			String accessKeyId,
			String secretAccessKey,
			String region,
			String bucketName) {
		this.accessEndpointUrl = accessEndpointUrl;
		this.accessKeyId = accessKeyId;
		this.secretAccessKey = secretAccessKey;
		this.region = region;
		this.bucketName = bucketName;
	}

	public static S3Settings createDefault() {
		return new S3Settings(
			defaultAccessEndpointUrl,
			null,
			null,
			defaultRegion,
			null
		);
	}
}
