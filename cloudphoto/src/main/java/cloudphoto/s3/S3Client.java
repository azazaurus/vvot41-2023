package cloudphoto.s3;

import cloudphoto.common.valueerrorresult.*;

public interface S3Client {
	cloudphoto.common.errorresult.Result<String> createBucketIfNotExists(String name);

	cloudphoto.common.errorresult.Result<String> uploadObject(
		String bucketName,
		String objectKey,
		String contentType,
		byte[] objectContent);
}
