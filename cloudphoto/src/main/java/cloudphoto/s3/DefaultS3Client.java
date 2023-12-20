package cloudphoto.s3;

import cloudphoto.common.*;
import cloudphoto.common.valueerrorresult.*;
import software.amazon.awssdk.core.checksums.*;
import software.amazon.awssdk.core.sync.*;
import software.amazon.awssdk.services.s3.model.*;

import java.util.*;

public class DefaultS3Client implements S3Client {
	private static final Base64.Encoder base64Encoder = Base64.getEncoder();

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

	private static String calculateBase64EncodedMd5(byte[] content) {
		var md5 = new Md5Checksum();
		md5.update(content);
		var md5Bytes = md5.getChecksumBytes();

		return base64Encoder.encodeToString(md5Bytes);
	}
}
