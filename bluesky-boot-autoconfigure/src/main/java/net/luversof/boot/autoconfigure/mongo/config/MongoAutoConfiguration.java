package net.luversof.boot.autoconfigure.mongo.config;


import java.util.stream.Collectors;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.mongo.MongoClientFactory;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;


@Configuration(value = "_blueskyMongoAutoConfiguration", proxyBeanMethods = false)
@ConditionalOnClass(MongoClient.class)
@EnableConfigurationProperties(MongoProperties.class)
public class MongoAutoConfiguration {
	
	@Bean
	public MongoPropertiesBeanFactoryPostProcessor mongoPropertiesBeanFactoryPostProcessor() {
		return new MongoPropertiesBeanFactoryPostProcessor();
	}
	
	@Bean
	public MongoPropertiesBeanPostProcessor mongoPropertiesBeanPostProcessor() {
		return new MongoPropertiesBeanPostProcessor();
	}
	
	@Bean
	@Primary
	public MongoClient emptyMongoClient(ObjectProvider<MongoClientSettingsBuilderCustomizer> builderCustomizers, MongoClientSettings settings) {
		return new MongoClientFactory(builderCustomizers.orderedStream().collect(Collectors.toList())).createMongoClient(settings);
	}

}
