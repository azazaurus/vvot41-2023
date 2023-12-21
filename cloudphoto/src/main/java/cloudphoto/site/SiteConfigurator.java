package cloudphoto.site;

import cloudphoto.*;
import cloudphoto.albums.*;
import cloudphoto.albums.repositories.*;
import cloudphoto.configuration.settings.*;
import cloudphoto.s3.*;
import cloudphoto.site.links.*;
import cloudphoto.site.repositories.*;
import org.springframework.context.annotation.*;

import java.util.function.*;

@Configuration
public class SiteConfigurator {
	@Bean
	public SitePageTemplateRepository sitePageTemplateRepository() {
		return new ResourceSitePageTemplateRepository(Application.class.getClassLoader());
	}

	@Bean
	public SitePageFactory sitePageFactory(SitePageTemplateRepository sitePageTemplateRepository) {
		return new SitePageFactory(sitePageTemplateRepository);
	}

	@Bean
	public SiteLinksFactory siteLinksFactory() {
		return new SiteLinksFactory();
	}

	@Bean
	public SiteRepository siteRepository(Supplier<S3Settings> s3SettingsProvider, S3Client s3Client) {
		return new S3BucketSiteRepository(s3SettingsProvider, s3Client);
	}

	@Bean
	public SiteService siteService(
			AlbumService albumService,
			S3BucketAlbumRepository s3BucketAlbumRepository,
			Supplier<S3Settings> s3SettingsProvider,
			SiteLinksFactory siteLinksFactory,
			SitePageFactory sitePageFactory,
			SiteRepository siteRepository) {
		return new SiteService(
			albumService,
			s3BucketAlbumRepository,
			s3SettingsProvider,
			siteLinksFactory,
			sitePageFactory,
			siteRepository);
	}
}
