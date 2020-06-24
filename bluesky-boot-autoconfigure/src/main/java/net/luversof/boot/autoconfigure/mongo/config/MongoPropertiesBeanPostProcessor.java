package net.luversof.boot.autoconfigure.mongo.config;

import java.text.MessageFormat;
import java.util.stream.Collectors;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.mongo.MongoClientFactory;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.PriorityOrdered;

import com.mongodb.MongoClientSettings;

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
		var environment = applicationContext.getEnvironment();
		var builderCustomizers = applicationContext.getBeanProvider(MongoClientSettingsBuilderCustomizer.class);
		var settings = applicationContext.getBeanProvider(MongoClientSettings.class);
		
		mongoProperties.getConnectionMap().forEach((key, value) -> {
			var blueskyMongoProperties = mongoProperties.getConnectionMap().get(key);

			var mongoClient = new MongoClientFactory(blueskyMongoProperties, environment, builderCustomizers.orderedStream().collect(Collectors.toList())).createMongoClient(settings.getIfAvailable());
			
			var mongoClientBeanName = MessageFormat.format(mongoClientBeanNameFormat, key);
			autowireCapableBeanFactory.destroySingleton(mongoClientBeanName);
			autowireCapableBeanFactory.registerSingleton(mongoClientBeanName, mongoClient);
		});
		
		return bean;
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
