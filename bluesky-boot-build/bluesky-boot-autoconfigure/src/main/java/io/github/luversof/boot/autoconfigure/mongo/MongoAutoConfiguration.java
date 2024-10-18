package io.github.luversof.boot.autoconfigure.mongo;


import java.util.Map;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.mongodb.client.MongoClient;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Mongo support.
 * @author bluesky
 *
 */
@AutoConfiguration("blueskyBootMongoAutoConfiguration")
@ConditionalOnClass(MongoClient.class)
@EnableConfigurationProperties(MongoProperties.class)
@ConditionalOnProperty(prefix= "bluesky-boot.mongodb.default-mongo-properties", name = "host")
public class MongoAutoConfiguration {

    @Bean
    MongoPropertiesBeanFactoryPostProcessor mongoPropertiesBeanFactoryPostProcessor() {
        return new MongoPropertiesBeanFactoryPostProcessor();
    }

    @Bean
    MongoPropertiesBeanPostProcessor mongoPropertiesBeanPostProcessor() {
        return new MongoPropertiesBeanPostProcessor();
    }

    @Bean
    @Primary
    MongoClient emptyMongoClient(MongoProperties mongoProperties, ObjectProvider<MongoClientSettingsBuilderCustomizer> builderCustomizers,  Map<String, MongoClient> mongoClientMap) {
    	if (!mongoClientMap.isEmpty()) {
    		return mongoClientMap.entrySet().stream().findFirst().get().getValue();
    	}
    	
        return MongoUtil.getMongoClient(mongoProperties.getDefaultMongoProperties(), builderCustomizers, MongoUtil.getDefaultMongoClientSettings(mongoProperties));
    }

}
