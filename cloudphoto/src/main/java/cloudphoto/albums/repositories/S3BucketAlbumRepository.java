package cloudphoto.albums.repositories;

import cloudphoto.common.valueerrorresult.*;
import cloudphoto.configuration.settings.*;
import cloudphoto.s3.*;

import java.util.*;
import java.util.function.*;

public class S3BucketAlbumRepository implements AlbumRepository {
	private static final String objectKeysDelimiter = "/";

	private final S3ObjectKeyEncoder s3ObjectKeyEncoder;
	private final Supplier<S3Settings> s3SettingsProvider;
	private final S3Client s3Client;

	public S3BucketAlbumRepository(
			S3ObjectKeyEncoder s3ObjectKeyEncoder,
			Supplier<S3Settings> s3SettingsProvider,
			S3Client s3Client) {
		this.s3ObjectKeyEncoder = s3ObjectKeyEncoder;
		this.s3SettingsProvider = s3SettingsProvider;
		this.s3Client = s3Client;
	}

	@Override
	public Result<List<ObjectKey>, List<String>> getPhotoFileNames(String albumName) {
		var bucketName = s3SettingsProvider.get().bucketName;
		var objectKeysResult = s3Client.searchObjectKeysByPrefix(
			bucketName,
			s3ObjectKeyEncoder.encode(albumName) + objectKeysDelimiter);
		if (objectKeysResult.isFailure())
			return Result.fail(List.of(objectKeysResult.getError()));

		var decodedObjectKeys = new ArrayList<ObjectKey>();
		var errors = new ArrayList<String>();
		for (var objectKeyToDecode : objectKeysResult.getValue()) {
			var decodedObjectKeyResult = decode(objectKeyToDecode);
			if (decodedObjectKeyResult.isFailure()) {
				errors.add("Invalid photo. " + decodedObjectKeyResult.getError());
				continue;
			}

			decodedObjectKeys.add(decodedObjectKeyResult.getValue());
		}

		return errors.size() > 0
			? new Result<>(decodedObjectKeys, errors)
			: Result.success(decodedObjectKeys);
	}

	@Override
	public Result<byte[], String> downloadPhoto(String albumName, String photoFileName) {
		var bucketName = s3SettingsProvider.get().bucketName;
		var photoObjectKey = encode(albumName, photoFileName);
		return s3Client.getObject(bucketName, photoObjectKey);
	}

	@Override
	public cloudphoto.common.errorresult.Result<String> createAlbumIfNotExists(String albumName) {
		return cloudphoto.common.errorresult.Result.success();
	}

	@Override
	public cloudphoto.common.errorresult.Result<String> uploadPhoto(
			String albumName,
			String photoFileName,
			byte[] photoContent) {
		var bucketName = s3SettingsProvider.get().bucketName;
		var photoObjectKey = encode(albumName, photoFileName);
		return s3Client.uploadObject(bucketName, photoObjectKey, "image/jpeg", photoContent);
	}

	private String encode(String albumName, String photoFileName) {
		return s3ObjectKeyEncoder.encode(albumName)
			+ objectKeysDelimiter
			+ s3ObjectKeyEncoder.encode(photoFileName);
	}

	private Result<ObjectKey, String> decode(String objectKey) {
		var namesDelimiterIndex = objectKey.lastIndexOf(objectKeysDelimiter);
		if (namesDelimiterIndex < 0)
			return Result.fail("Invalid photo object key: \"" + objectKey + "\"");
		if (namesDelimiterIndex == objectKey.length() - 1)
			return Result.fail("Empty photo file name in object key \"" + objectKey + "\"");

		var encodedAlbumName = objectKey.substring(0, namesDelimiterIndex);
		var encodedPhotoFileName = objectKey.substring(namesDelimiterIndex + 1);

		try {
			return Result.success(
				new ObjectKey(
					s3ObjectKeyEncoder.decode(encodedAlbumName),
					s3ObjectKeyEncoder.decode(encodedPhotoFileName)));
		}
		catch (Exception e) {
			return Result.fail("Can't decode photo object key \"" + objectKey + "\". " + e.getMessage());
		}
	}
}
