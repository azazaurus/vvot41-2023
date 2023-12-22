package cloudphoto.configuration;

import cloudphoto.common.*;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.support.*;
import org.springframework.core.*;

import java.lang.reflect.*;

public class LazyRegistrationBeanFactoryPostProcessor
		extends BaseProviderRegistrationBeanFactoryPostProcessor<Lazy<?>> {
	public LazyRegistrationBeanFactoryPostProcessor(Class<?>... excludedTypes) {
		super(excludedTypes);
	}

	@Override
	protected String getProviderBeanName(String beanName) {
		return Lazy.class.getName() + "<" + beanName + ">";
	}

	@Override
	protected ResolvableType getProviderType(Type beanClass) {
		return ResolvableType.forClassWithGenerics(Lazy.class, ResolvableType.forType(beanClass));
	}

	@Override
	protected Lazy<?> createProvider(
			DefaultListableBeanFactory beanFactory,
			String beanName,
			Type beanClass) {
		return createProvider(beanFactory, beanName);
	}

	private static <T> Lazy<T> createProvider(BeanFactory beanFactory, String beanName) {
		return new Lazy<>(() -> (T)beanFactory.getBean(beanName));
	}
}
