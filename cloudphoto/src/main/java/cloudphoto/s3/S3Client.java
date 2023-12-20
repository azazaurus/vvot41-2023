package cloudphoto.s3;

import cloudphoto.common.valueerrorresult.*;

import java.util.*;

public interface S3Client {
	Result<List<String>, String> searchObjectKeysByPrefix(String bucketName, String prefix);

	Result<byte[], String> getObject(String bucketName, String objectKey);

	cloudphoto.common.errorresult.Result<String> createBucketIfNotExists(String name);

	cloudphoto.common.errorresult.Result<String> uploadObject(
		String bucketName,
		String objectKey,
		String contentType,
		byte[] objectContent);
}
