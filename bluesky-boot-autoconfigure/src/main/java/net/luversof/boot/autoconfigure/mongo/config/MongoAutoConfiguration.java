package net.luversof.boot.autoconfigure.mongo.config;


import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;


@Configuration(value = "_blueskyMongoAutoConfiguration", proxyBeanMethods = false)
@ConditionalOnClass(MongoClient.class)
@EnableConfigurationProperties(MongoProperties.class)
public class MongoAutoConfiguration {
	
	@Bean
	public MongoPropertiesBeanPostProcessor mongoPropertiesBeanPostProcessor() {
		return new MongoPropertiesBeanPostProcessor();
	}
	
	@Bean
	@Primary
	public MongoClient emptyMongoClient() {
		return MongoClients.create();
	}

}
