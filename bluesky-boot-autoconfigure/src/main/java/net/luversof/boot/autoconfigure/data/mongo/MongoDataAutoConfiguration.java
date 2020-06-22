package net.luversof.boot.autoconfigure.data.mongo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import com.mongodb.client.MongoClient;

import net.luversof.boot.autoconfigure.mongo.config.MongoAutoConfiguration;
import net.luversof.boot.autoconfigure.mongo.config.MongoProperties;

@Configuration(value = "_blueskyMongoDataAutoConfiguration", proxyBeanMethods = false)
@ConditionalOnClass({ MongoClient.class, MongoTemplate.class })
@AutoConfigureAfter(MongoAutoConfiguration.class)
public class MongoDataAutoConfiguration {
	
	@Autowired
	private MongoProperties mongoProperties;

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
