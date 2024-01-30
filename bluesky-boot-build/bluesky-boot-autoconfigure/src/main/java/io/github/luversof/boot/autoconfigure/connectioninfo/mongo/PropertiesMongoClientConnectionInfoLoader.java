package io.github.luversof.boot.autoconfigure.connectioninfo.mongo;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;

import com.mongodb.client.MongoClient;

import io.github.luversof.boot.connectioninfo.ConnectionInfoCollector;
import io.github.luversof.boot.connectioninfo.ConnectionInfoLoader;
import io.github.luversof.boot.connectioninfo.ConnectionInfoLoaderProperties;
import lombok.Getter;

/**
 * mongoClient는 따로 bean을 등록하는 처리가 되어 있으므로 등록된 bean을 반환하는 처리만 함
 */
public class PropertiesMongoClientConnectionInfoLoader implements ConnectionInfoLoader<MongoClient> {
	
	private ApplicationContext applicationContext;
	
	private ConnectionInfoLoaderProperties connectionInfoLoaderProperties;
	
	private String mongoClientBeanNameFormat = "{0}MongoClient"; 
	
	@Getter
	private String loaderKey = "properties-mongoclient";
	
	public PropertiesMongoClientConnectionInfoLoader(ApplicationContext applicationContext, ConnectionInfoLoaderProperties connectionInfoLoaderProperties) {
		this.applicationContext = applicationContext;
		this.connectionInfoLoaderProperties = connectionInfoLoaderProperties;
	}

	@Override
	public ConnectionInfoCollector<MongoClient> load(List<String> connectionList) {
		var mongoClientMap = new HashMap<String, MongoClient>();
		
		connectionList.forEach(connection -> {
			var mongoClientBeanName = MessageFormat.format(mongoClientBeanNameFormat, connection);
			var mongoClientBean = applicationContext.getBean(mongoClientBeanName, MongoClient.class);
			mongoClientMap.put(connection, mongoClientBean);
		});
		
		return () -> mongoClientMap;
	}

	@Override
	public ConnectionInfoCollector<MongoClient> load() {
		
		if (connectionInfoLoaderProperties == null 
				|| connectionInfoLoaderProperties.getLoaders() == null 
				|| !connectionInfoLoaderProperties.getLoaders().containsKey(getLoaderKey())
				|| CollectionUtils.isEmpty(connectionInfoLoaderProperties.getLoaders().get(getLoaderKey()).getConnections())) {
			return Collections::emptyMap;
		}

		List<String> connectionList = connectionInfoLoaderProperties.getLoaders().get(getLoaderKey()).getConnections().values().stream().flatMap(List::stream).distinct().toList(); 
		
		return load(connectionList);
	}
	

}
