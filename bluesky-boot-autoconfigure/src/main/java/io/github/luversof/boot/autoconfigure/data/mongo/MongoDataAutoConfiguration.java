package io.github.luversof.boot.autoconfigure.data.mongo;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import com.mongodb.client.MongoClient;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Spring Data's Mongo support.
 * @author bluesky
 *
 */
@AutoConfiguration("_blueskyBootMongoDataAutoConfiguration")
@ConditionalOnClass({ MongoClient.class, MongoTemplate.class })
// @AutoConfigureAfter(MongoAutoConfiguration.class)
public class MongoDataAutoConfiguration {

    @Bean
    MongoDataPropertiesBeanFactoryPostProcessor mongoDataPropertiesBeanFactoryPostProcessor() {
        return new MongoDataPropertiesBeanFactoryPostProcessor();
    }

    @Bean
    MongoDataPropertiesBeanPostProcessor mongoDataPropertiesBeanPostProcessor() {
        return new MongoDataPropertiesBeanPostProcessor();
    }

    @Bean
    @Primary
    MongoDatabaseFactory configMongoDatabaseFactory(MongoClient emptyMongoClient) {
        return new SimpleMongoClientDatabaseFactory(emptyMongoClient, "config");
    }

    @Bean
    @Primary
	MongoTemplate emptyMongoTemplate(@Qualifier("configMongoDatabaseFactory") MongoDatabaseFactory configMongoDatabaseFactory) {
        return new MongoTemplate(configMongoDatabaseFactory);
    }
}
