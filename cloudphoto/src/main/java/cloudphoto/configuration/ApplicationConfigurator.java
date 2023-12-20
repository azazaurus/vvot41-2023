package cloudphoto.configuration;

import cloudphoto.*;
import cloudphoto.albums.*;
import cloudphoto.cli.*;
import cloudphoto.common.Lazy;
import cloudphoto.configuration.settings.*;
import cloudphoto.filesystem.*;
import cloudphoto.logs.*;
import cloudphoto.s3.*;
import org.springframework.beans.factory.config.*;
import org.springframework.context.*;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.*;

import java.util.*;
import java.util.function.*;

@Configuration
public class ApplicationConfigurator {
	private static final Class<?>[] diConfiguratorClasses = {
		ApplicationConfigurator.class,
		ApplicationSettingsConfigurator.class,
		CliConfigurator.class,
		S3Configurator.class,
		FileSystemConfigurator.class,
		LogConfigurator.class,
		AlbumConfigurator.class
	};

	private static final Class<?>[] excludedFromProviderRegistrationTypes = { Supplier.class, Lazy.class };

	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public Application application(List<CliCommand> cliCommandExecutors) {
		return new Application(cliCommandExecutors);
	}

	public static ApplicationContext createDiContainer() {
		var diContainer = new AnnotationConfigApplicationContext();
		diContainer.addBeanFactoryPostProcessor(new LazyInitializationBeanFactoryPostProcessor());
		diContainer.addBeanFactoryPostProcessor(
			new SupplierRegistrationBeanFactoryPostProcessor(excludedFromProviderRegistrationTypes));
		diContainer.addBeanFactoryPostProcessor(
			new LazyRegistrationBeanFactoryPostProcessor(excludedFromProviderRegistrationTypes));

		diContainer.register(diConfiguratorClasses);
		diContainer.refresh();

		return diContainer;
	}
}
