package cloudphoto.albums.repositories;

import cloudphoto.common.valueerrorresult.*;

public interface AlbumRepository {
	cloudphoto.common.errorresult.Result<String> createAlbumIfNotExists(String albumName);

	cloudphoto.common.errorresult.Result<String> uploadPhoto(
		String albumName,
		String photoFileName,
		byte[] photoContent);
}
