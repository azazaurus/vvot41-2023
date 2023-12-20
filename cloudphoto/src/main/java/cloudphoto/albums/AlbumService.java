package cloudphoto.albums;

import cloudphoto.albums.repositories.*;
import cloudphoto.common.valueerrorresult.*;
import cloudphoto.filesystem.*;
import cloudphoto.logs.*;

import java.nio.file.*;

public class AlbumService {
	private final FileService fileService;
	private final AlbumRepository albumRepository;
	private final Log log;

	public AlbumService(FileService fileService, AlbumRepository albumRepository, Log log) {
		this.fileService = fileService;
		this.albumRepository = albumRepository;
		this.log = log;
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

		var photoFileNamesResult = albumRepository.getPhotoFileNames(albumName);
		var decodedObjectKeys = photoFileNamesResult.getValue();
		if (decodedObjectKeys == null)
			return cloudphoto.common.errorresult.Result.fail(
				"Can't fetch list of photos in album \"" + albumName + "\" to download. "
					+ photoFileNamesResult.getError());

		var success = photoFileNamesResult.isSuccess();
		if (success) {
			if (decodedObjectKeys.isEmpty())
				return cloudphoto.common.errorresult.Result.fail("Album \"" + albumName + "\" is not found");
		}
		else
			log.warn(String.join(System.lineSeparator(), photoFileNamesResult.getError()));

		for (var decodedObjectKey : decodedObjectKeys) {
			if (!decodedObjectKey.albumName.equals(albumName)) {
				log.warn(
					"Photo \""
						+ decodedObjectKey.photoFileName
						+ "\" belongs to other album \""
						+ decodedObjectKey.albumName
						+ "\"");
				continue;
			}

			var photoContentResult = albumRepository.downloadPhoto(albumName, decodedObjectKey.photoFileName);
			if (photoContentResult.isFailure()) {
				success = false;
				log.warn(
					"Can't download photo \""
						+ decodedObjectKey.photoFileName
						+ "\" from album \""
						+ albumName
						+ "\". "
						+ photoContentResult.getError());
				continue;
			}

			var uploadPhotoResult = fileService.savePhoto(
				photoDirectoryPath,
				decodedObjectKey.photoFileName,
				photoContentResult.getValue());
			if (uploadPhotoResult.isFailure()) {
				success = false;
				log.warn(
					"Can't save photo \""
						+ decodedObjectKey.photoFileName
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
}
