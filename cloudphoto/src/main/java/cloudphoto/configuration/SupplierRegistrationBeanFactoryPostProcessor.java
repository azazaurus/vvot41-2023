package cloudphoto.configuration;

import org.springframework.beans.factory.*;
import org.springframework.beans.factory.support.*;
import org.springframework.core.*;

import java.lang.reflect.*;
import java.util.function.*;

public class SupplierRegistrationBeanFactoryPostProcessor
		extends BaseProviderRegistrationBeanFactoryPostProcessor<Supplier<?>> {
	public SupplierRegistrationBeanFactoryPostProcessor(Class<?>... excludedTypes) {
		super(excludedTypes);
	}

	@Override
	protected String getProviderBeanName(String beanName) {
		return Supplier.class.getName() + "<" + beanName + ">";
	}

	@Override
	protected ResolvableType getProviderType(Type beanClass) {
		return ResolvableType.forClassWithGenerics(Supplier.class, ResolvableType.forType(beanClass));
	}

	@Override
	protected Supplier<?> createProvider(
			DefaultListableBeanFactory beanFactory,
			String beanName,
			Type beanClass) {
		return createProvider(beanFactory, beanName);
	}

	private static <T> Supplier<T> createProvider(BeanFactory beanFactory, String beanName) {
		return () -> (T)beanFactory.getBean(beanName);
	}
}
