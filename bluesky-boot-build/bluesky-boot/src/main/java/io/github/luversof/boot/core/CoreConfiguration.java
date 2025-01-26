package io.github.luversof.boot.core;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({
	CoreBaseProperties.class,
	CoreProperties.class,
	CoreModuleProperties.class
})
public class CoreConfiguration {
	
	@Bean
	BlueskyPropertiesBeanFactoryPostProcessor blueskyPropertiesBeanFactoryPostProcessor() {
		return new BlueskyPropertiesBeanFactoryPostProcessor();
	}
	
	@Bean
	BlueskyModulePropertiesBeanFactoryPostProcessor blueskyModulePropertiesBeanFactoryPostProcessor() {
		return new BlueskyModulePropertiesBeanFactoryPostProcessor();
	}
}
