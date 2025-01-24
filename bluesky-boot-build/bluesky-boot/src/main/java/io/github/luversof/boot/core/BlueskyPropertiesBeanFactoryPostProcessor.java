package io.github.luversof.boot.core;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * BlueskyProperties bean dependencies 설정
 * - 모든 BlueskyProperties는 CoreProperties 의존 (CoreBaseProperties 제외)
 * - CoreProperties는 CoreBaseProperties 의존
 * 
 */
public class BlueskyPropertiesBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		String[] beanNames = beanFactory.getBeanNamesForType(BlueskyProperties.class);
		for (String beanName : beanNames) {
			if (beanName.equals(CoreBaseProperties.BEAN_NAME) || beanName.equals(CoreProperties.BEAN_NAME)) {
				continue;
			}
			beanFactory.getBeanDefinition(beanName).setDependsOn(CoreProperties.BEAN_NAME);
		}
		
		beanFactory.getBeanDefinition(CoreProperties.BEAN_NAME).setDependsOn(CoreBaseProperties.BEAN_NAME);
	}
	
}
