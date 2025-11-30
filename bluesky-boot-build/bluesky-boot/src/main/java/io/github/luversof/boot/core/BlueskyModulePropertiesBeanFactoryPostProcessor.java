package io.github.luversof.boot.core;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.ResolvableType;

/**
 * BlueskyModuleProperties bean dependencies 설정
 * - 모든 BlueskyModuleProperties는 CoreModuleProperties 의존
 * - BlueskyGroupProperties 구현이 있으면 의존 
 */
public class BlueskyModulePropertiesBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		String[] beanNames = beanFactory.getBeanNamesForType(BlueskyModuleProperties.class);
		
		// CoreModuleProperties 의존 설정
		for (String beanName : beanNames) {
			if (beanName.equals(CoreModuleProperties.BEAN_NAME)) {
				beanFactory.getBeanDefinition(beanName).setDependsOn(CoreBaseProperties.BEAN_NAME);
				continue;
			}
			beanFactory.getBeanDefinition(beanName).setDependsOn(CoreModuleProperties.BEAN_NAME);
		}
		
		
		// BlueskyGroupProperties 의존 설정
		for (String beanName: beanNames) {
			var type = beanFactory.getType(beanName);
			
			ResolvableType resolvableType = ResolvableType.forClass(type).as(BlueskyModuleProperties.class);
			
			ResolvableType generic = resolvableType.getGeneric(0);
			
			for (var groupPropertiesBeanName :  beanFactory.getBeanNamesForType(ResolvableType.forClassWithGenerics(BlueskyGroupProperties.class, generic))) {
				beanFactory.getBeanDefinition(beanName).setDependsOn(groupPropertiesBeanName);
			}
		}
	}
	
	

}