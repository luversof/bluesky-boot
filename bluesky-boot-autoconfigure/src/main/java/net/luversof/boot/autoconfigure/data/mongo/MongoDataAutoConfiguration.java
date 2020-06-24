package net.luversof.boot.autoconfigure.data.mongo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import com.mongodb.client.MongoClient;

@Configuration(value = "_blueskyMongoDataAutoConfiguration", proxyBeanMethods = false)
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
	public MongoDatabaseFactory emptyMongoDatabaseFactory(MongoClient emptyMongoClient) {
		return new SimpleMongoClientDatabaseFactory(emptyMongoClient, "test");
	}
	
	@Bean
	@Primary
	public MongoTemplate emptyMongoTemplate(MongoDatabaseFactory emptyMongoDatabaseFactory) {
		return new MongoTemplate(emptyMongoDatabaseFactory);
	}
}
