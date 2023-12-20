package cloudphoto.albums;

import cloudphoto.albums.repositories.*;
import cloudphoto.common.valueerrorresult.*;
import cloudphoto.filesystem.*;
import cloudphoto.logs.*;

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
}
