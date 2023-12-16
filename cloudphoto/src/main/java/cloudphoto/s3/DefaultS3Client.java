package cloudphoto.s3;

import cloudphoto.common.*;
import cloudphoto.common.valueerrorresult.*;
import software.amazon.awssdk.services.s3.model.*;

public class DefaultS3Client implements S3Client {
	private final Lazy<software.amazon.awssdk.services.s3.S3Client> s3Client;

	public DefaultS3Client(Lazy<software.amazon.awssdk.services.s3.S3Client> s3Client) {
		this.s3Client = s3Client;
	}

	@Override
	public cloudphoto.common.errorresult.Result<String> createBucketIfNotExists(String name) {
		var createBucketRequest = CreateBucketRequest.builder()
			.bucket(name)
			.build();

		try {
			s3Client.get().createBucket(createBucketRequest);
		}
		catch (BucketAlreadyOwnedByYouException e) {
			return cloudphoto.common.errorresult.Result.success();
		}
		catch (BucketAlreadyExistsException e) {
			return cloudphoto.common.errorresult.Result.fail(e.getMessage());
		}

		return cloudphoto.common.errorresult.Result.success();
	}
}
