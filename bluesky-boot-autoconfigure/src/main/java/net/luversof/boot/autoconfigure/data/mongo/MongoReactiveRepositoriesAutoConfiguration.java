package net.luversof.boot.autoconfigure.data.mongo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.mongodb.reactivestreams.client.MongoClient;


@Configuration("_blueskyBootMongoReactiveRepositoriesAutoConfiguration")
@ConditionalOnClass({ MongoClient.class, ReactiveMongoRepository.class })
@PropertySource("classpath:data/mongodb/data-mongodb-${net-profile}.properties")
@EnableMongoAuditing
public class MongoReactiveRepositoriesAutoConfiguration {

}
