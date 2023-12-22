package cloudphoto.site;

import cloudphoto.albums.*;
import cloudphoto.albums.repositories.*;
import cloudphoto.common.valueerrorresult.*;
import cloudphoto.configuration.settings.*;
import cloudphoto.site.links.*;
import cloudphoto.site.repositories.*;

import java.util.*;
import java.util.function.*;

public class SiteService {
	private static final String websiteUrlTemplate = "http://%s.website.yandexcloud.net/";
	private static final String indexPagePath = "index.html";
	private static final String errorPagePath = "error.html";

	private final AlbumService albumService;
	private final S3BucketAlbumRepository s3BucketAlbumRepository;
	private final Supplier<S3Settings> s3SettingsProvider;
	private final SiteLinksFactory siteLinksFactory;
	private final SitePageFactory sitePageFactory;
	private final SiteRepository siteRepository;

	public SiteService(
			AlbumService albumService,
			S3BucketAlbumRepository s3BucketAlbumRepository,
			Supplier<S3Settings> s3SettingsProvider,
			SiteLinksFactory siteLinksFactory,
			SitePageFactory sitePageFactory,
			SiteRepository siteRepository) {
		this.albumService = albumService;
		this.s3BucketAlbumRepository = s3BucketAlbumRepository;
		this.s3SettingsProvider = s3SettingsProvider;
		this.siteLinksFactory = siteLinksFactory;
		this.sitePageFactory = sitePageFactory;
		this.siteRepository = siteRepository;
	}

	public Result<String, String> publishGalleria() {
		var albumNamesResult = albumService.listAlbums();
		if (albumNamesResult.isFailure())
			return Result.fail(albumNamesResult.getError());

		var albumNames = albumNamesResult.getValue();
		var albumPageLinks = siteLinksFactory.createAlbumPageLinks(albumNames);

		var pagesToUpload = new HashMap<String, String>();
		pagesToUpload.put(indexPagePath, sitePageFactory.createIndexPage(albumPageLinks));
		pagesToUpload.put(errorPagePath, sitePageFactory.createErrorPage());

		var bucketName = s3SettingsProvider.get().bucketName;
		for (var albumPageLink : albumPageLinks) {
			var photoFileNameToObjectKeyMapResult = s3BucketAlbumRepository
				.getPhotoFileNameToObjectKeyMap(albumPageLink.albumName);
			if (photoFileNameToObjectKeyMapResult.isFailure())
				return Result.fail(photoFileNameToObjectKeyMapResult.getError());

			var photoLinks = siteLinksFactory.createPhotoPageLinks(
				bucketName,
				photoFileNameToObjectKeyMapResult.getValue());
			pagesToUpload.put(albumPageLink.url, sitePageFactory.createAlbumPage(photoLinks));
		}

		var pagesUploadResult = uploadPages(pagesToUpload);
		if (pagesUploadResult.isFailure())
			return Result.fail("Can't upload pages. " + pagesUploadResult.getError());

		var websitePublicationResult = siteRepository.publishWebsite(indexPagePath, errorPagePath);
		if (websitePublicationResult.isFailure())
			return Result.fail("Can't publish web site. " + websitePublicationResult.getError());
		
		return Result.success(String.format(websiteUrlTemplate, bucketName));
	}

	private cloudphoto.common.errorresult.Result<String> uploadPages(Map<String, String> pagePathToContentMap) {
		for (var page : pagePathToContentMap.entrySet()) {
			var pageUploadResult = siteRepository.uploadPage(page.getKey(), page.getValue());
			if (pageUploadResult.isFailure())
				return pageUploadResult;
		}

		return cloudphoto.common.errorresult.Result.success();
	}
}
