package net.luversof.boot.autoconfigure.data.mongo;

import java.text.MessageFormat;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import com.mongodb.client.MongoClient;

import net.luversof.boot.autoconfigure.mongo.config.MongoProperties;

/**
 * MongoProperties가 생성된 이후 Data Mongo의 mongoTemplate 빈 생성용 processor
 * @author luver
 *
 */
public class MongoDataPropertiesBeanPostProcessor implements BeanPostProcessor, ApplicationContextAware, Ordered {
	
	private ApplicationContext applicationContext;
	
	private String mongoClientBeanNameFormat = "{0}MongoClient";
	private String mongoDatabaseFactoryBeanNameFormat = "{0}MongoDatabaseFactory";
	private String mongoTemplateBeanNameFormat = "{0}MongoTemplate";

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
		var mongoClientMap = applicationContext.getBeansOfType(MongoClient.class);
		
		mongoProperties.getConnectionMap().forEach((key, value) -> {
			var blueskyMongoProperties = mongoProperties.getConnectionMap().get(key);
			var mongoClient = mongoClientMap.get(MessageFormat.format(mongoClientBeanNameFormat, key));
			
			var mongoDatabaseFactory = new SimpleMongoClientDatabaseFactory(mongoClient, blueskyMongoProperties.getMongoClientDatabase());
			autowireCapableBeanFactory.registerSingleton(MessageFormat.format(mongoDatabaseFactoryBeanNameFormat, key), mongoDatabaseFactory);
			
			var mongoTemplate = new MongoTemplate(mongoDatabaseFactory);
			autowireCapableBeanFactory.registerSingleton(MessageFormat.format(mongoTemplateBeanNameFormat, key), mongoTemplate);
		});
		
		return bean;
	}



	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}



	@Override
	public int getOrder() {
		return 1000;
	}

}
