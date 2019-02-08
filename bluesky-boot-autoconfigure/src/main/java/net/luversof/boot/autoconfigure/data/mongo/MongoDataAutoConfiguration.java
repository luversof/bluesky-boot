package net.luversof.boot.autoconfigure.data.mongo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.MongoClient;

@Configuration("_blueskyBootMongoDataAutoConfiguration")
@ConditionalOnClass({ MongoClient.class, com.mongodb.client.MongoClient.class, MongoTemplate.class })
@PropertySource("classpath:data/mongodb/data-mongodb-${net-profile}.properties")
public class MongoDataAutoConfiguration {

}
