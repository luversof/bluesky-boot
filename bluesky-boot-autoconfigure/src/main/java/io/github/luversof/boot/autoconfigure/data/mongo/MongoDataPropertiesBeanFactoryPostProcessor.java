package io.github.luversof.boot.autoconfigure.data.mongo;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import com.mongodb.client.MongoClients;

public class MongoDataPropertiesBeanFactoryPostProcessor implements BeanFactoryPostProcessor, ApplicationContextAware {
	
	private ApplicationContext applicationContext;
	
	private String propertiesPrefix = "bluesky-boot.mongodb.connection-map";
	
	private String mongoDatabaseFactoryBeanNameFormat = "{0}MongoDatabaseFactory";
	private String mongoTemplateBeanNameFormat = "{0}MongoTemplate";

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		var emptyMongoDatabaseFactory = new SimpleMongoClientDatabaseFactory(MongoClients.create(), "config");
		var emptyMongoTemplate = new MongoTemplate(emptyMongoDatabaseFactory);
		
		getPropertiesMapKeySet().forEach(key -> {
			beanFactory.registerSingleton(MessageFormat.format(mongoDatabaseFactoryBeanNameFormat, key), emptyMongoDatabaseFactory);
			beanFactory.registerSingleton(MessageFormat.format(mongoTemplateBeanNameFormat, key), emptyMongoTemplate);
		});

	}

	private Map<String, String> getPropertiesMap() {
		var properties = new HashMap<String, String>();
		var environment = applicationContext.getEnvironment();
		if (environment instanceof ConfigurableEnvironment configurableEnvironment) {
		    for (PropertySource<?> propertySource : configurableEnvironment.getPropertySources()) {
		        if (propertySource instanceof EnumerablePropertySource) {
		            for (String key : ((EnumerablePropertySource<?>) propertySource).getPropertyNames()) {
		                if (key.startsWith(propertiesPrefix)) {
		                    properties.put(key, (String) propertySource.getProperty(key));
		                }
		            }
		        }
		    }
		}
		return properties;
	}
	
	private Set<String> getPropertiesMapKeySet() {
		Map<String, String> propertiesMap = getPropertiesMap();
		var keySet = new HashSet<String>();
		propertiesMap.forEach((key, value) -> keySet.add(key.replace(propertiesPrefix, "").split("\\.")[1]));
		return keySet;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
