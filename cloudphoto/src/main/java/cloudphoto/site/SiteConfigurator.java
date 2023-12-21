package cloudphoto.site;

import cloudphoto.*;
import cloudphoto.site.repositories.*;
import org.springframework.context.annotation.*;

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
}
