package net.luversof.boot.autoconfigure.mongo.config;

import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;

public class MongoTemplateBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {
	
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		Properties properties = System.getProperties();
// 		MongoProperties bean = beanFactory.getBean(MongoProperties.class);
		System.out.println("TEST!!!!");
		// new MongoTemplate(mongoDatabaseFactory);
		// beanFactory.registerSington();
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		// TODO Auto-generated method stub

	}

}
