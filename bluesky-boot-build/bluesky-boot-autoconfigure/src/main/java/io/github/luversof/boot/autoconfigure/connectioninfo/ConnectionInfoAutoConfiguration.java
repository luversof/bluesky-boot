package io.github.luversof.boot.autoconfigure.connectioninfo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mongodb.client.MongoClient;

import io.github.luversof.boot.autoconfigure.connectioninfo.mongo.PropertiesMongoClientConnectionInfoLoader;
import io.github.luversof.boot.connectioninfo.ConnectionInfo;
import io.github.luversof.boot.connectioninfo.ConnectionInfoLoader;
import io.github.luversof.boot.connectioninfo.ConnectionInfoProperties;
import io.github.luversof.boot.connectioninfo.ConnectionInfoRegistry;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for ConnectionInfo support.
 * @author bluesky
 *
 */
@AutoConfiguration("blueskyBootConnectionInfoAutoConfiguration")
public class ConnectionInfoAutoConfiguration {
	
	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass(MongoClient.class)
	@ConditionalOnProperty(prefix = "bluesky-boot.connection-info.loaders", name = "properties-mongoclient.enabled", havingValue = "true")
	static class PropertiesMongoClientConnectionInfoConfiguration {
		
		@Bean
		PropertiesMongoClientConnectionInfoLoader propertiesMongoClientConnectionInfoLoader(ApplicationContext applicationContext, ConnectionInfoProperties connectionInfoProperties) {
			return new PropertiesMongoClientConnectionInfoLoader(applicationContext, connectionInfoProperties);
		}
		
//		@Bean
//		ConnectionInfoCollector<MongoClient> propertiesMongoClientConnectionInfoCollector(PropertiesMongoClientConnectionInfoLoader propertiesMongoClientConnectionInfoLoader) {
//			return propertiesMongoClientConnectionInfoLoader.load();
//		}
		
//		@Bean
//		MongoClientConnectionInfoCollectorDecorator mongoClientConnectionInfoCollectorDecorator(Map<String, ConnectionInfoLoader<MongoClient>> mongoClientConnectionInfoLoaderMap) {
//			return new MongoClientConnectionInfoCollectorDecorator(mongoClientConnectionInfoLoaderMap);
//		}
		
		@Bean
		ConnectionInfoRegistry<MongoClient> mongoClientConnectionInfoRegistry(List<ConnectionInfoLoader> connectionInfoLoaderList) {
			var connectionInfoList = new ArrayList<ConnectionInfo<MongoClient>>();
			connectionInfoLoaderList.forEach(connectionInfoLoader -> connectionInfoList.addAll(connectionInfoLoader.load()));
			return () -> connectionInfoList;
		}
	}
	
}
