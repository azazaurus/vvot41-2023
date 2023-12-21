package cloudphoto.s3;

import cloudphoto.common.valueerrorresult.*;

import java.util.*;

public interface S3Client {
	Result<List<String>, String> getObjectKeys(String bucketName);

	Result<List<String>, String> searchObjectKeysByPrefix(String bucketName, String prefix);

	Result<byte[], String> getObject(String bucketName, String objectKey);

	cloudphoto.common.errorresult.Result<String> createBucketIfNotExists(String name);

	cloudphoto.common.errorresult.Result<String> uploadObject(
		String bucketName,
		String objectKey,
		String contentType,
		byte[] objectContent);

	cloudphoto.common.errorresult.Result<String> deleteObject(String bucketName, String objectKey);

	cloudphoto.common.errorresult.Result<String> deleteObjects(String bucketName,	List<String> objectKeys);

	cloudphoto.common.errorresult.Result<String> publishBucketWebsite(
		String bucketName,
		String indexDocumentPath,
		String errorDocumentPath);
}
