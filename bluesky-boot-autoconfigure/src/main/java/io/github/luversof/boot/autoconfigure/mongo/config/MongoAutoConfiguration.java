package io.github.luversof.boot.autoconfigure.mongo.config;


import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.mongo.MongoClientFactory;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;


@AutoConfiguration("_blueskyBootMongoAutoConfiguration")
@ConditionalOnClass(MongoClient.class)
@EnableConfigurationProperties(MongoProperties.class)
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
    MongoClientSettings mongoClientSettings() {
        return MongoClientSettings.builder().build();
    }

    @Bean
    @Primary
    MongoClient emptyMongoClient(ObjectProvider<MongoClientSettingsBuilderCustomizer> builderCustomizers, MongoClientSettings settings) {
        return new MongoClientFactory(builderCustomizers.orderedStream().toList()).createMongoClient(settings);
    }

}
