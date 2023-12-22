package cloudphoto.site.repositories;

import java.io.*;
import java.nio.charset.*;
import java.util.*;

public class ResourceSitePageTemplateRepository implements SitePageTemplateRepository {
	private static final Charset resourceCharset = StandardCharsets.UTF_8;

	private final ClassLoader classLoader;
	private final HashMap<String, String> templatesMap = new HashMap<>();

	public ResourceSitePageTemplateRepository(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	@Override
	public String getPageTemplate(String pageTemplateResourceName) {
		var template = templatesMap.getOrDefault(pageTemplateResourceName, null);
		if (template == null) {
			template = loadPageTemplate(pageTemplateResourceName, classLoader);
			templatesMap.put(pageTemplateResourceName, template);
		}
		return template;
	}

	private static String loadPageTemplate(String templateResourceName, ClassLoader classLoader) {
		try (var templateStream = classLoader.getResourceAsStream(templateResourceName)) {
			if (templateStream == null)
				throw new MissingResourceException(
					"Missing resource " + templateResourceName,
					classLoader.getName(),
					templateResourceName);
			var templateBytes = templateStream.readAllBytes();
			return new String(templateBytes, resourceCharset);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
