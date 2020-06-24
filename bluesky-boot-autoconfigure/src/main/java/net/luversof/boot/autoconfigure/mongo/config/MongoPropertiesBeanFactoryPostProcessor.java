package net.luversof.boot.autoconfigure.mongo.config;

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

import com.mongodb.client.MongoClients;

public class MongoPropertiesBeanFactoryPostProcessor implements BeanFactoryPostProcessor, ApplicationContextAware {
	
	private ApplicationContext applicationContext;
	
	private String propertiesPrefix = "bluesky-modules.mongodb.connection-map";
	
	private String mongoClientBeanNameFormat = "{0}MongoClient";

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		System.out.println("TEST!!!!");
		getPropertiesMapKeySet().forEach(key -> {
			System.out.println("key : " + key);
			beanFactory.registerSingleton(MessageFormat.format(mongoClientBeanNameFormat, key), MongoClients.create());
		});
		
		System.out.println("TEST!!!!");

	}
	
	private Map<String, String> getPropertiesMap() {
		var properties = new HashMap<String, String>();
		var environment = applicationContext.getEnvironment();
		if (environment instanceof ConfigurableEnvironment) {
		    for (PropertySource<?> propertySource : ((ConfigurableEnvironment) environment).getPropertySources()) {
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
		propertiesMap.forEach((key, value) -> {
			 keySet.add(key.replace(propertiesPrefix, "").split("\\.")[1]);
		});
		return keySet;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	

}
