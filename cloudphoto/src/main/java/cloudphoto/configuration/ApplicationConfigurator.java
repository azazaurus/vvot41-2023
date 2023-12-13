package cloudphoto.configuration;

import cloudphoto.*;
import cloudphoto.configuration.settings.*;
import org.springframework.context.*;
import org.springframework.context.annotation.*;

import java.util.function.*;

@Configuration
public class ApplicationConfigurator {
	private static final Class<?>[] diConfiguratorClasses = {
		ApplicationConfigurator.class,
		ApplicationSettingsConfigurator.class
	};

	private static final Class<?>[] excludedFromProviderRegistrationTypes = { Supplier.class };

	@Bean
	public Application application() {
		return new Application();
	}

	public static ApplicationContext createDiContainer() {
		var diContainer = new AnnotationConfigApplicationContext();
		diContainer.addBeanFactoryPostProcessor(new LazyInitializationBeanFactoryPostProcessor());
		diContainer.addBeanFactoryPostProcessor(
			new SupplierRegistrationBeanFactoryPostProcessor(excludedFromProviderRegistrationTypes));

		diContainer.register(diConfiguratorClasses);
		diContainer.refresh();

		return diContainer;
	}
}
