package io.github.luversof.boot.autoconfigure.data.mongo;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import com.mongodb.client.MongoClient;

@AutoConfiguration("_blueskyBootMongoDataAutoConfiguration")
@ConditionalOnClass({ MongoClient.class, MongoTemplate.class })
// @AutoConfigureAfter(MongoAutoConfiguration.class)
public class MongoDataAutoConfiguration {
	
	@Bean
	public MongoDataPropertiesBeanFactoryPostProcessor mongoDataPropertiesBeanFactoryPostProcessor() {
		return new MongoDataPropertiesBeanFactoryPostProcessor();
	}

	@Bean
	public MongoDataPropertiesBeanPostProcessor mongoDataPropertiesBeanPostProcessor() {
		return new MongoDataPropertiesBeanPostProcessor();
	}
	
	@Bean
	@Primary
	public MongoDatabaseFactory configMongoDatabaseFactory(MongoClient emptyMongoClient) {
		return new SimpleMongoClientDatabaseFactory(emptyMongoClient, "config");
	}
	
	@Bean
	@Primary
	public MongoTemplate emptyMongoTemplate(@Qualifier("configMongoDatabaseFactory") MongoDatabaseFactory configMongoDatabaseFactory) {
		return new MongoTemplate(configMongoDatabaseFactory);
	}
}
