package io.github.luversof.boot.autoconfigure.mongo.config;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.PriorityOrdered;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClients;
import com.mongodb.connection.ClusterConnectionMode;

public class MongoPropertiesBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware, PriorityOrdered {

	private ApplicationContext applicationContext;
	
	private String mongoClientBeanNameFormat = "{0}MongoClient";
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (!(bean instanceof MongoProperties)) {
			return bean;
		}
		
		if (applicationContext == null) {
			return bean;
		}
		
		var autowireCapableBeanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
		var mongoProperties = (MongoProperties) bean;
		var builderCustomizersBeanProvider = applicationContext.getBeanProvider(MongoClientSettingsBuilderCustomizer.class);
		var settingsBeanProvider = applicationContext.getBeanProvider(MongoClientSettings.class);
		
		var settings = settingsBeanProvider.getIfAvailable() == null ? defaultMongoClientSettings(mongoProperties) : settingsBeanProvider.getIfAvailable();
		
		mongoProperties.getConnectionMap().forEach((key, value) -> {
			var blueskyMongoProperties = mongoProperties.getConnectionMap().get(key);

			// Spring의 MongoClientFactory는 단일 host에 대해서만 설정이 가능하여 별도 처리함

			var builder = settings != null ? MongoClientSettings.builder(settings) : MongoClientSettings.builder();
			builder.uuidRepresentation(blueskyMongoProperties.getUuidRepresentation());
			if (blueskyMongoProperties.getUri() != null) {
				builder.applyConnectionString(new ConnectionString(blueskyMongoProperties.getUri()));
			} else if (blueskyMongoProperties.getHosts() != null && blueskyMongoProperties.getHosts().length > 0) {
				var serverAddressList = new ArrayList<ServerAddress>();
				for (String host : blueskyMongoProperties.getHosts()) {
					if (host.contains(":")) {
						String[] sa = host.split(":");
						serverAddressList.add(new ServerAddress(sa[0], Integer.parseInt(sa[1])));
					} else {
						serverAddressList.add(new ServerAddress(host));
					}
				}
				builder.applyToClusterSettings(cluster -> cluster.hosts(serverAddressList).mode(ClusterConnectionMode.MULTIPLE));
			} else if (blueskyMongoProperties.getHost() != null && blueskyMongoProperties.getPort() != null) {
				builder.applyToClusterSettings(cluster -> cluster.hosts(Collections.singletonList(new ServerAddress(blueskyMongoProperties.getHost(), blueskyMongoProperties.getPort()))));
			}
			
			if (blueskyMongoProperties.getUsername() != null && blueskyMongoProperties.getPassword() != null) {
				String database = (blueskyMongoProperties.getAuthenticationDatabase() != null) ? blueskyMongoProperties.getAuthenticationDatabase() : blueskyMongoProperties.getMongoClientDatabase();
				builder.credential((MongoCredential.createCredential(blueskyMongoProperties.getUsername(), database, blueskyMongoProperties.getPassword())));
			}
			
			if (blueskyMongoProperties.getReplicaSetName() != null) {
				builder.applyToClusterSettings(cluster -> cluster.requiredReplicaSetName(blueskyMongoProperties.getReplicaSetName()));
			}
			
			for (MongoClientSettingsBuilderCustomizer customizer : builderCustomizersBeanProvider.orderedStream().collect(Collectors.toList())) {
				customizer.customize(builder);
			}
			
			var mongoClient = MongoClients.create(builder.build());
			
			var mongoClientBeanName = MessageFormat.format(mongoClientBeanNameFormat, key);
			autowireCapableBeanFactory.destroySingleton(mongoClientBeanName);
			autowireCapableBeanFactory.registerSingleton(mongoClientBeanName, mongoClient);
		});
		
		return bean;
	}
	
	
	private MongoClientSettings defaultMongoClientSettings(MongoProperties mongoProperties) {

		var builder = MongoClientSettings.builder();

		var connectionPoolSettings = mongoProperties.getDefaultConnectionPoolSettings();
		if (connectionPoolSettings != null) {
			builder.applyToConnectionPoolSettings(b -> b.maxSize(connectionPoolSettings.getMaxSize())
					.minSize(connectionPoolSettings.getMinSize())
					.maxWaitTime(connectionPoolSettings.getMaxWaitTimeMS(), TimeUnit.MILLISECONDS)
					.maxConnectionLifeTime(connectionPoolSettings.getMaxConnectionLifeTimeMS(), TimeUnit.MILLISECONDS)
					.maxConnectionIdleTime(connectionPoolSettings.getMaxConnectionIdleTimeMS(), TimeUnit.MILLISECONDS)
					.maintenanceInitialDelay(connectionPoolSettings.getMaintenanceInitialDelayMS(),
							TimeUnit.MILLISECONDS)
					.maintenanceFrequency(connectionPoolSettings.getMaintenanceFrequencyMS(), TimeUnit.MILLISECONDS));
		}

		if (mongoProperties.getDefaultReadConcernLevel() != null) {
			builder.readConcern(new ReadConcern(mongoProperties.getDefaultReadConcernLevel()));
		}

		if (mongoProperties.getDefaultReadPreference() != null) {
			builder.readPreference(ReadPreference.valueOf(mongoProperties.getDefaultReadPreference()));
		}

		var writeConcern = mongoProperties.getDefaultWriteConcern();
		if (writeConcern != null) {
			builder.writeConcern(WriteConcern.valueOf(writeConcern.getW())
					.withWTimeout(writeConcern.getWTimeoutMS(), TimeUnit.MILLISECONDS)
					.withJournal(writeConcern.isJournal()));
		}

		return builder.build();
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
			this.applicationContext = applicationContext;
	}

	@Override
	public int getOrder() {
		return 0;
	}

}
