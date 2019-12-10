package net.luversof.boot.autoconfigure.data.mongo;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.SimpleReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.mongodb.reactivestreams.client.MongoClient;


@Configuration("_blueskyBootMongoReactiveRepositoriesAutoConfiguration")
@ConditionalOnClass({ MongoClient.class, ReactiveMongoRepository.class })
@PropertySource("classpath:data/mongodb/data-mongodb-${net-profile}.properties")
@EnableMongoAuditing
public class MongoReactiveRepositoriesAutoConfiguration {

	@Primary
	@Bean
	public SimpleReactiveMongoDatabaseFactory configReactiveMongoDatabaseFactory(MongoClient mongoClient) {
		return new SimpleReactiveMongoDatabaseFactory(mongoClient, "config");
	}
	
	@Primary
	@Bean
	public ReactiveMongoTemplate configReactiveMongoTemplate(@Qualifier("configReactiveMongoDatabaseFactory") ReactiveMongoDatabaseFactory reactiveMongoDatabaseFactory, MongoConverter converter) {
		return new ReactiveMongoTemplate(reactiveMongoDatabaseFactory, converter);
	}
}
