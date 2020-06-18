package net.luversof.boot.autoconfigure.mongo.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.mongodb.client.MongoClient;


@Configuration(value = "_blueskyMongoAutoConfiguration", proxyBeanMethods = false)
@ConditionalOnClass(MongoClient.class)
@EnableConfigurationProperties(MongoProperties.class)
@PropertySource(value = "classpath:mongo/mongo-${net-profile}.properties", ignoreResourceNotFound = true)
public class MongoAutoConfiguration {

}
