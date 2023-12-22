package cloudphoto.configuration;

import org.springframework.beans.*;
import org.springframework.beans.factory.config.*;
import org.springframework.beans.factory.support.*;
import org.springframework.core.*;

import java.lang.reflect.*;
import java.util.*;
import java.util.stream.*;

public abstract class BaseProviderRegistrationBeanFactoryPostProcessor<Provider>
		implements BeanFactoryPostProcessor {
	private final Set<Class<?>> excludedFromRegistrationTypes;

	public BaseProviderRegistrationBeanFactoryPostProcessor(Class<?>... excludedFromRegistrationTypes) {
		this.excludedFromRegistrationTypes = Arrays
			.stream(excludedFromRegistrationTypes)
			.collect(Collectors.toSet());
	}

	@Override
	public void postProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactoryInterface) throws BeansException {
		if (!(beanFactoryInterface instanceof DefaultListableBeanFactory))
			throw new IllegalArgumentException(
				"Only " + DefaultListableBeanFactory.class.getSimpleName() + " is supported");

		var beanFactory = (DefaultListableBeanFactory)beanFactoryInterface;
		for (String beanName : beanFactory.getBeanDefinitionNames()) {
			BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
			if (beanDefinition instanceof RootBeanDefinition)
				this.postProcess(beanFactory, beanName, (RootBeanDefinition)beanDefinition);
		}
	}

	protected abstract String getProviderBeanName(String beanName);

	protected abstract ResolvableType getProviderType(Type beanClass);

	protected abstract Provider createProvider(
		DefaultListableBeanFactory beanFactory,
		String beanName,
		Type beanClass);

	private void postProcess(
			DefaultListableBeanFactory beanFactory,
			String beanName,
			RootBeanDefinition beanDefinition) {
		var beanFactoryMethod = beanDefinition.getResolvedFactoryMethod();
		if (beanFactoryMethod == null)
			return;

		var beanClass = beanFactoryMethod.getGenericReturnType();
		if (beanClass instanceof Class && excludedFromRegistrationTypes.contains(beanClass))
			return;

		var providerBeanName = getProviderBeanName(beanName);
		var providerBeanDefinition = BeanDefinitionBuilder.rootBeanDefinition(
				getProviderType(beanClass),
				() -> createProvider(beanFactory, beanName, beanClass))
			.setLazyInit(true)
			.getBeanDefinition();
		beanFactory.registerBeanDefinition(providerBeanName, providerBeanDefinition);
	}
}
