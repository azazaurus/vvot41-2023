package cloudphoto.configuration.settings;

import java.net.*;

public class S3Settings {
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
}
