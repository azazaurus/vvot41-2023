package cloudphoto.albums;

import cloudphoto.albums.repositories.*;
import cloudphoto.common.valueerrorresult.*;
import cloudphoto.filesystem.*;
import cloudphoto.logs.*;

import java.nio.file.*;
import java.util.*;

public class AlbumService {
	private final FileService fileService;
	private final AlbumRepository albumRepository;
	private final Log log;

	public AlbumService(FileService fileService, AlbumRepository albumRepository, Log log) {
		this.fileService = fileService;
		this.albumRepository = albumRepository;
		this.log = log;
	}

	public Result<List<String>, String> listPhotos(String albumName) {
		var photoFileNamesResult = internalListPhotos(albumName);
		if (photoFileNamesResult.isFailure()) {
			var errorMessage = String.join(System.lineSeparator(), photoFileNamesResult.getError());
			return Result.fail(errorMessage);
		}

		return Result.success(photoFileNamesResult.getValue());
	}

	public cloudphoto.common.errorresult.Result<String> uploadPhotos(
			String albumName,
			String photoDirectoryPath) {
		var photoFileNamesResult = fileService.getPhotoFileNames(photoDirectoryPath);
		if (photoFileNamesResult.isFailure())
			return cloudphoto.common.errorresult.Result.fail(
				"Can't get list of photos to upload. " + photoFileNamesResult.getError());

		var photoFileNames = photoFileNamesResult.getValue();
		if (photoFileNames.isEmpty())
			return cloudphoto.common.errorresult.Result.fail(
				"No photos are found in " + photoDirectoryPath);

		var success = true;
		for (var photoFileName : photoFileNames) {
			var photoContentResult = fileService.loadPhoto(photoDirectoryPath, photoFileName);
			if (photoContentResult.isFailure()) {
				success = false;
				log.warn("Can't load photo file \"" + photoFileName + "\". " + photoContentResult.getError());
				continue;
			}

			var uploadPhotoResult = albumRepository.uploadPhoto(
				albumName,
				photoFileName,
				photoContentResult.getValue());
			if (uploadPhotoResult.isFailure()) {
				success = false;
				log.warn(
					"Can't upload photo \"" + photoFileName + "\" to album. " + uploadPhotoResult.getError());
			}
		}

		return success
			? cloudphoto.common.errorresult.Result.success()
			: cloudphoto.common.errorresult.Result.fail("");
	}

	public cloudphoto.common.errorresult.Result<String> downloadPhotos(
			String albumName,
			String photoDirectoryPath) {
		if (!Files.isDirectory(Path.of(photoDirectoryPath)))
			return cloudphoto.common.errorresult.Result.fail(
				"Directory \"" + photoDirectoryPath + "\" is not found");

		var photoFileNamesResult = internalListPhotos(albumName);
		var photoFileNames = photoFileNamesResult.getValue();
		var success = photoFileNamesResult.isSuccess();
		if (!success) {
			log.warn(String.join(System.lineSeparator(), photoFileNamesResult.getError()));
			if (photoFileNames == null)
				return cloudphoto.common.errorresult.Result.fail("");
		}

		for (var photoFileName : photoFileNames) {
			var photoContentResult = albumRepository.downloadPhoto(albumName, photoFileName);
			if (photoContentResult.isFailure()) {
				success = false;
				log.warn(
					"Can't download photo \""
						+ photoFileName
						+ "\" from album \""
						+ albumName
						+ "\". "
						+ photoContentResult.getError());
				continue;
			}

			var uploadPhotoResult = fileService.savePhoto(
				photoDirectoryPath,
				photoFileName,
				photoContentResult.getValue());
			if (uploadPhotoResult.isFailure()) {
				success = false;
				log.warn(
					"Can't save photo \""
						+ photoFileName
						+ "\" to directory \""
						+ photoDirectoryPath
						+ "\". "
						+ uploadPhotoResult.getError());
			}
		}

		return success
			? cloudphoto.common.errorresult.Result.success()
			: cloudphoto.common.errorresult.Result.fail("");
	}

	private Result<List<String>, List<String>> internalListPhotos(String albumName) {
		var photoFileNamesResult = albumRepository.getPhotoFileNames(albumName);
		var decodedObjectKeys = photoFileNamesResult.getValue();
		if (decodedObjectKeys == null)
			return Result.fail(
				List.of(
					"Can't fetch list of photos in album \"" + albumName + "\". "
						+ String.join(". ", photoFileNamesResult.getError())));

		if (photoFileNamesResult.isSuccess() && decodedObjectKeys.isEmpty())
			return Result.fail(List.of("Album \"" + albumName + "\" is not found"));

		var photoFileNames = new ArrayList<String>();
		var errors = photoFileNamesResult.isFailure()
			? photoFileNamesResult.getError()
			: new ArrayList<String>();
		for (var decodedObjectKey : decodedObjectKeys) {
			if (!decodedObjectKey.albumName.equals(albumName)) {
				errors.add(
					"Photo \""
						+ decodedObjectKey.photoFileName
						+ "\" belongs to other album \""
						+ decodedObjectKey.albumName
						+ "\"");
				continue;
			}

			photoFileNames.add(decodedObjectKey.photoFileName);
		}

		return errors.size() > 0
			? new Result<>(photoFileNames, errors)
			: Result.success(photoFileNames);
	}
}
