package io.github.luversof.boot.core;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties
public class CoreConfiguration {
	
	@Bean(CoreBaseProperties.BEAN_NAME)
	CoreBaseProperties coreBaseProperties() {
		return new CoreBaseProperties();
	}
	
	@Bean(CoreProperties.BEAN_NAME)
	CoreProperties coreProperties() {
		return new CoreProperties();
	}
	
	@Bean(CoreModuleProperties.BEAN_NAME)
	CoreModuleProperties coreModuleProperties() {
		return new CoreModuleProperties();
	}
	
	@Bean
	BlueskyPropertiesBeanFactoryPostProcessor blueskyPropertiesBeanFactoryPostProcessor() {
		return new BlueskyPropertiesBeanFactoryPostProcessor();
	}
	
	@Bean
	BlueskyModulePropertiesBeanFactoryPostProcessor blueskyModulePropertiesBeanFactoryPostProcessor() {
		return new BlueskyModulePropertiesBeanFactoryPostProcessor();
	}
}
