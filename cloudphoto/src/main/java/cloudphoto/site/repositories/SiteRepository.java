package cloudphoto.site.repositories;

import cloudphoto.common.errorresult.*;

public interface SiteRepository {
	Result<String> uploadPage(String pagePath, String pageContent);

	Result<String> publishWebsite(String indexPagePath, String errorPagePath);
}
