package cloudphoto.s3;

import cloudphoto.common.*;
import cloudphoto.common.valueerrorresult.*;
import software.amazon.awssdk.core.checksums.*;
import software.amazon.awssdk.core.sync.*;
import software.amazon.awssdk.services.s3.model.*;

import java.util.*;
import java.util.stream.*;

public class DefaultS3Client implements S3Client {
	private static final Base64.Encoder base64Encoder = Base64.getEncoder();

	private final Lazy<software.amazon.awssdk.services.s3.S3Client> s3Client;

	public DefaultS3Client(Lazy<software.amazon.awssdk.services.s3.S3Client> s3Client) {
		this.s3Client = s3Client;
	}

	@Override
	public Result<List<String>, String> getObjectKeys(String bucketName) {
		var listObjectsRequest = ListObjectsV2Request.builder().bucket(bucketName).build();
		return listObjects(listObjectsRequest);
	}

	@Override
	public Result<List<String>, String> searchObjectKeysByPrefix(String bucketName, String prefix) {
		var listObjectsRequest = ListObjectsV2Request.builder()
			.bucket(bucketName)
			.prefix(prefix)
			.build();
		return listObjects(listObjectsRequest);
	}

	@Override
	public Result<byte[], String> getObject(String bucketName, String objectKey) {
		var getObjectRequest = GetObjectRequest.builder()
			.bucket(bucketName)
			.key(objectKey)
			.build();
		try {
			var objectStream = s3Client.get().getObject(getObjectRequest);
			var object = objectStream.readAllBytes();
			return Result.success(object);
		}
		catch (Exception e) {
			return Result.fail(e.getMessage());
		}
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

	@Override
	public cloudphoto.common.errorresult.Result<String> uploadObject(
			String bucketName,
			String objectKey,
			String contentType,
			byte[] objectContent) {
		var md5 = calculateBase64EncodedMd5(objectContent);
		var putFileRequest = PutObjectRequest.builder()
			.bucket(bucketName)
			.key(objectKey)
			.contentType(contentType)
			.contentMD5(md5)
			.contentLength((long)objectContent.length)
			.build();
		try {
			s3Client.get().putObject(putFileRequest, RequestBody.fromBytes(objectContent));
		}
		catch (Exception e) {
			return cloudphoto.common.errorresult.Result.fail(e.getMessage());
		}

		return cloudphoto.common.errorresult.Result.success();
	}

	@Override
	public cloudphoto.common.errorresult.Result<String> deleteObject(String bucketName, String objectKey) {
		var deleteObjectRequest = DeleteObjectRequest.builder()
			.bucket(bucketName)
			.key(objectKey)
			.build();
		try {
			s3Client.get().deleteObject(deleteObjectRequest);
		}
		catch (Exception e) {
			return cloudphoto.common.errorresult.Result.fail(e.getMessage());
		}

		return cloudphoto.common.errorresult.Result.success();
	}

	@Override
	public cloudphoto.common.errorresult.Result<String> deleteObjects(
			String bucketName,
			List<String> objectKeys) {
		var objectIds = objectKeys.stream()
			.map(objectKey -> ObjectIdentifier.builder().key(objectKey).build())
			.toArray(ObjectIdentifier[]::new);
		var deleteObjectsRequest = DeleteObjectsRequest.builder()
			.bucket(bucketName)
			.delete(Delete.builder().objects(objectIds).build())
			.build();
		try {
			var deleteObjectsResponse = s3Client.get().deleteObjects(deleteObjectsRequest);
			if (deleteObjectsResponse.hasErrors()) {
				var errorMessages = deleteObjectsResponse.errors().stream()
					.map(S3Error::message)
					.collect(Collectors.toList());
				return cloudphoto.common.errorresult.Result.fail(
					String.join(System.lineSeparator(), errorMessages));
			}

			return cloudphoto.common.errorresult.Result.success();
		}
		catch (Exception e) {
			return cloudphoto.common.errorresult.Result.fail(e.getMessage());
		}
	}

	@Override
	public cloudphoto.common.errorresult.Result<String> publishBucketWebsite(
			String bucketName,
			String indexDocumentPath,
			String errorDocumentPath) {
		var publicAccessResult = setUpPublicAccess(bucketName);
		if (publicAccessResult.isFailure())
			return publicAccessResult;

		return setUpWebsite(bucketName, indexDocumentPath, errorDocumentPath);
	}

	private Result<List<String>, String> listObjects(ListObjectsV2Request listObjectsRequest) {
		ListObjectsV2Response objectsResponse;
		try {
			objectsResponse = s3Client.get().listObjectsV2(listObjectsRequest);
		}
		catch (Exception e) {
			return Result.fail(e.getMessage());
		}

		if (!objectsResponse.hasContents())
			return Result.success(List.of());

		return Result.success(
			objectsResponse.contents().stream()
				.map(S3Object::key)
				.collect(Collectors.toList()));
	}

	private cloudphoto.common.errorresult.Result<String> setUpPublicAccess(String bucketName) {
		var publicAccessSetupRequest = PutBucketAclRequest.builder()
			.bucket(bucketName)
			.acl(BucketCannedACL.PUBLIC_READ)
			.build();

		try {
			s3Client.get().putBucketAcl(publicAccessSetupRequest);
			return cloudphoto.common.errorresult.Result.success();
		}
		catch (Exception e) {
			return cloudphoto.common.errorresult.Result.fail(e.getMessage());
		}
	}

	private cloudphoto.common.errorresult.Result<String> setUpWebsite(
			String bucketName,
			String indexDocumentPath,
			String errorDocumentPath) {
		var websiteConfiguration = WebsiteConfiguration.builder()
			.indexDocument(IndexDocument.builder().suffix(indexDocumentPath).build())
			.errorDocument(ErrorDocument.builder().key(errorDocumentPath).build())
			.build();
		var publishBucketWebsiteRequest = PutBucketWebsiteRequest.builder()
			.bucket(bucketName)
			.websiteConfiguration(websiteConfiguration)
			.build();

		try {
			s3Client.get().putBucketWebsite(publishBucketWebsiteRequest);
			return cloudphoto.common.errorresult.Result.success();
		}
		catch (Exception e) {
			return cloudphoto.common.errorresult.Result.fail(e.getMessage());
		}
	}

	private static String calculateBase64EncodedMd5(byte[] content) {
		var md5 = new Md5Checksum();
		md5.update(content);
		var md5Bytes = md5.getChecksumBytes();

		return base64Encoder.encodeToString(md5Bytes);
	}
}
