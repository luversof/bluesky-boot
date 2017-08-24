package net.luversof.boot.autoconfigure.data.mongo;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.core.MongoTemplate;

import com.mongodb.Mongo;

@Configuration("_blueskyBootMongoDataAutoConfiguration")
@ConditionalOnClass({ Mongo.class, MongoTemplate.class })
@PropertySource("classpath:data/mongodb/data-mongodb-${spring.profiles.active}.properties")
public class MongoDataAutoConfiguration {

}
