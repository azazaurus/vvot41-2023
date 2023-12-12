package cloudphoto.configuration;

import cloudphoto.*;
import org.springframework.context.*;
import org.springframework.context.annotation.*;

@Configuration
public class ApplicationConfigurator {
	private static final Class<?>[] diConfiguratorClasses = {
		ApplicationConfigurator.class
	};

	@Bean
	public Application application() {
		return new Application();
	}

	public static ApplicationContext createDiContainer() {
		var diContainer = new AnnotationConfigApplicationContext();
		diContainer.addBeanFactoryPostProcessor(new LazyInitializationBeanFactoryPostProcessor());

		diContainer.register(diConfiguratorClasses);
		diContainer.refresh();

		return diContainer;
	}
}
