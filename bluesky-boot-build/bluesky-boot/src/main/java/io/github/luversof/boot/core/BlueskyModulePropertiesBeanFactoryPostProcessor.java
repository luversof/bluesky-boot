package io.github.luversof.boot.core;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * BlueskyModuleProperties bean dependencies 설정
 * - CoreModuleProperties
 */
public class BlueskyModulePropertiesBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		String[] beanNames = beanFactory.getBeanNamesForType(BlueskyModuleProperties.class);
		for (String beanName : beanNames) {
			if (beanName.equals(CoreModuleProperties.BEAN_NAME)) {
				continue;
			}
			beanFactory.getBeanDefinition(beanName).setDependsOn(CoreModuleProperties.BEAN_NAME);
		}
	}

}