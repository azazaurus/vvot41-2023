package cloudphoto.albums.repositories;

import cloudphoto.common.valueerrorresult.*;
import cloudphoto.configuration.settings.*;
import cloudphoto.s3.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.*;

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
	public Result<Set<String>, String> getAlbumNames() {
		var bucketName = s3SettingsProvider.get().bucketName;
		var objectKeysResult = s3Client.getObjectKeys(bucketName);
		if (objectKeysResult.isFailure())
			return Result.fail(objectKeysResult.getError());

		var albumNames = new HashSet<String>();
		for (var objectKeyToDecode : objectKeysResult.getValue()) {
			var decodedObjectKeyResult = decode(objectKeyToDecode);
			if (decodedObjectKeyResult.isFailure())
				continue;

			albumNames.add(decodedObjectKeyResult.getValue().albumName);
		}

		return Result.success(albumNames);
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

	public Result<Map<String, String>, String> getPhotoFileNameToObjectKeyMap(String albumName) {
		var bucketName = s3SettingsProvider.get().bucketName;
		var objectKeysResult = s3Client.searchObjectKeysByPrefix(
			bucketName,
			s3ObjectKeyEncoder.encode(albumName) + objectKeysDelimiter);
		if (objectKeysResult.isFailure())
			return Result.fail(objectKeysResult.getError());

		var photoFileNameToObjectKeyMap = new HashMap<String, String>();
		var errors = new ArrayList<String>();
		for (var objectKeyToDecode : objectKeysResult.getValue()) {
			var decodedObjectKeyResult = decode(objectKeyToDecode);
			if (decodedObjectKeyResult.isFailure()) {
				errors.add("Invalid photo. " + decodedObjectKeyResult.getError());
				continue;
			}

			var decodedObjectKey = decodedObjectKeyResult.getValue();
			photoFileNameToObjectKeyMap.put(decodedObjectKey.photoFileName, objectKeyToDecode);
		}

		return errors.size() > 0
			? Result.fail(String.join(System.lineSeparator(), errors))
			: Result.success(photoFileNameToObjectKeyMap);
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

	@Override
	public cloudphoto.common.errorresult.Result<String> deletePhoto(String albumName, String photoFileName) {
		var bucketName = s3SettingsProvider.get().bucketName;
		var photoObjectKey = encode(albumName, photoFileName);
		return s3Client.deleteObject(bucketName, photoObjectKey);
	}

	@Override
	public cloudphoto.common.errorresult.Result<String> deletePhotos(
			String albumName,
			List<String> photoFileNames) {
		var bucketName = s3SettingsProvider.get().bucketName;
		var photoObjectKeys = photoFileNames.stream()
			.map(photoFileName -> encode(albumName, photoFileName))
			.collect(Collectors.toList());
		return s3Client.deleteObjects(bucketName, photoObjectKeys);
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
