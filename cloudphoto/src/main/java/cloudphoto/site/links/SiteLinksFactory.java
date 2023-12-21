package cloudphoto.site.links;

import java.util.*;

public class SiteLinksFactory {
	private static final String albumPageLinkTemplate = "album%d.html";
	private static final String photoPageUrlTemplate = "https://storage.yandexcloud.net/%s/%s";

	public List<AlbumPageLink> createAlbumPageLinks(List<String> albumNames) {
		var links = new ArrayList<AlbumPageLink>();
		for (var i = 0; i < albumNames.size(); ++i) {
			var albumPageLink = new AlbumPageLink(
				albumNames.get(i),
				String.format(albumPageLinkTemplate, i + 1));
			links.add(albumPageLink);
		}
		return links;
	}

	public List<PhotoLink> createPhotoPageLinks(
			String bucketName,
			Map<String, String> sourceFileNameToObjectKeyMap) {
		var links = new ArrayList<PhotoLink>();
		for (var photo : sourceFileNameToObjectKeyMap.entrySet()) {
			var photoFileName = photo.getKey();
			var photoObjectKey = photo.getValue();

			var photoUrl = String.format(photoPageUrlTemplate, bucketName, photoObjectKey);
			links.add(new PhotoLink(photoFileName, photoUrl));
		}
		return links;
	}
}
