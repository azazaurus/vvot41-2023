package cloudphoto.configuration;

import org.springframework.beans.*;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.config.*;
import org.springframework.beans.factory.support.*;

public final class LazyInitializationBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		for (String beanName : beanFactory.getBeanDefinitionNames()) {
			BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
			if (beanDefinition instanceof AbstractBeanDefinition)
				this.postProcess(beanFactory, beanName, (AbstractBeanDefinition)beanDefinition);
		}
	}

	private void postProcess(
			ConfigurableListableBeanFactory beanFactory,
			String beanName,
			AbstractBeanDefinition beanDefinition) {
		Boolean lazyInit = beanDefinition.getLazyInit();
		if (lazyInit != null)
			return;

		Class<?> beanType = this.getBeanType(beanFactory, beanName);
		if (beanType != SmartInitializingSingleton.class)
			beanDefinition.setLazyInit(true);
	}

	private Class<?> getBeanType(ConfigurableListableBeanFactory beanFactory, String beanName) {
		try {
			return beanFactory.getType(beanName, false);
		} catch (NoSuchBeanDefinitionException e) {
			return null;
		}
	}
}
