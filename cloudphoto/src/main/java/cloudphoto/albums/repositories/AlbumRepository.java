package cloudphoto.albums.repositories;

import cloudphoto.common.valueerrorresult.*;

import java.util.*;

public interface AlbumRepository {
	Result<Set<String>, String> getAlbumNames();

	Result<List<ObjectKey>, List<String>> getPhotoFileNames(String albumName);

	Result<byte[], String> downloadPhoto(String albumName, String photoFileName);

	cloudphoto.common.errorresult.Result<String> createAlbumIfNotExists(String albumName);

	cloudphoto.common.errorresult.Result<String> uploadPhoto(
		String albumName,
		String photoFileName,
		byte[] photoContent);

	cloudphoto.common.errorresult.Result<String> deletePhoto(String albumName, String photoFileName);

	cloudphoto.common.errorresult.Result<String> deletePhotos(String albumName, List<String> photoFileNames);

	class ObjectKey {
		public final String albumName;
		public final String photoFileName;

		public ObjectKey(String albumName, String photoFileName) {
			this.albumName = albumName;
			this.photoFileName = photoFileName;
		}
	}
}
