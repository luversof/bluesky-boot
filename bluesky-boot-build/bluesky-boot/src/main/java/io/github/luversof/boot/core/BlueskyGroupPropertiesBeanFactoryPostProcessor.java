package io.github.luversof.boot.core;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

public class BlueskyGroupPropertiesBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		String[] beanNames = beanFactory.getBeanNamesForType(BlueskyGroupProperties.class);
		
		// CoreBaseProperties 의존 설정
		for (String beanName : beanNames) {
			beanFactory.getBeanDefinition(beanName).setDependsOn(CoreBaseProperties.BEAN_NAME);
		}
		
		
	}

}
