package cloudphoto.site;

import cloudphoto.site.links.*;
import cloudphoto.site.repositories.*;

import java.util.*;

public class SitePageFactory {
	private final SitePageTemplateRepository sitePageTemplateRepository;

	public SitePageFactory(SitePageTemplateRepository sitePageTemplateRepository) {
		this.sitePageTemplateRepository = sitePageTemplateRepository;
	}

	public String createIndexPage(List<AlbumPageLink> albumLinks) {
		var indexPageTemplate = sitePageTemplateRepository.getPageTemplate(
			SitePageResourceNames.indexPageTemplate);
		var albumLinkTemplate = sitePageTemplateRepository.getPageTemplate(
			SitePageResourceNames.albumLinkTemplate);

		var albumLinkHtmls = new ArrayList<String>();
		for (var albumLink : albumLinks) {
			var albumLinkHtml = String.format(albumLinkTemplate, albumLink.url, albumLink.albumName);
			albumLinkHtmls.add(albumLinkHtml);
		}
		var albumLinksHtml = String.join(System.lineSeparator(), albumLinkHtmls);

		return String.format(indexPageTemplate, albumLinksHtml);
	}

	public String createErrorPage() {
		return sitePageTemplateRepository.getPageTemplate(
			SitePageResourceNames.errorPageTemplate);
	}

	public String createAlbumPage(List<PhotoLink> photoLinks) {
		var albumPageTemplate = sitePageTemplateRepository.getPageTemplate(
			SitePageResourceNames.albumPageTemplate);
		var photoLinkTemplate = sitePageTemplateRepository.getPageTemplate(
			SitePageResourceNames.photoLinkTemplate);

		var photoLinkHtmls = new ArrayList<String>();
		for (var photoLink : photoLinks) {
			var photoLinkHtml = String.format(photoLinkTemplate, photoLink.url, photoLink.sourceFileName);
			photoLinkHtmls.add(photoLinkHtml);
		}
		var photoLinksHtml = String.join(System.lineSeparator(), photoLinkHtmls);

		return String.format(albumPageTemplate, photoLinksHtml);
	}
}
