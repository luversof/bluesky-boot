//package io.github.luversof.boot.autoconfigure.connectioninfo.mongo;
//
//import java.text.MessageFormat;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//import org.springframework.context.ApplicationContext;
//import org.springframework.util.CollectionUtils;
//
//import com.mongodb.client.MongoClient;
//
//import io.github.luversof.boot.connectioninfo.ConnectionInfo;
//import io.github.luversof.boot.connectioninfo.ConnectionInfoKey;
//import io.github.luversof.boot.connectioninfo.ConnectionInfoLoader;
//import io.github.luversof.boot.connectioninfo.ConnectionInfoProperties;
//import io.github.luversof.boot.connectioninfo.mongodb.MongoDbMongoClientConnectionConfig;
//import io.github.luversof.boot.connectioninfo.mongodb.MongoDbMongoClientConnectionConfigReader;
//import lombok.Getter;
//
///**
// * mongoClient는 따로 bean을 등록하는 처리가 되어 있으므로 등록된 bean을 반환하는 처리만 함
// */
//public class PropertiesMongoClientConnectionInfoLoader implements ConnectionInfoLoader<MongoClient, MongoDbMongoClientConnectionConfig, MongoDbMongoClientConnectionConfigReader> {
//	
//	private ApplicationContext applicationContext;
//	
//	
//	private ConnectionInfoProperties connectionInfoProperties;
//	private String mongoClientBeanNameFormat = "{0}MongoClient"; 
//	
//	@Getter
//	private String loaderKey = "properties-mongoclient";
//	
//	public PropertiesMongoClientConnectionInfoLoader(ApplicationContext applicationContext, ConnectionInfoProperties connectionInfoProperties) {
//		this.applicationContext = applicationContext;
//		this.connectionInfoProperties = connectionInfoProperties;
//	}
//
//	@Override
//	public List<ConnectionInfo<MongoClient>> load(List<String> connectionList) {
//		var connectionInfoList = new ArrayList<ConnectionInfo<MongoClient>>();
//		connectionList.forEach(connection -> {
//			var mongoClientBeanName = MessageFormat.format(mongoClientBeanNameFormat, connection);
//			var mongoClientBean = applicationContext.getBean(mongoClientBeanName, MongoClient.class);
//			connectionInfoList.add(new ConnectionInfo<>(new ConnectionInfoKey(getLoaderKey(), connection), mongoClientBean));
//		});
//		
//		return connectionInfoList;
//	}
//
//	@Override
//	public List<ConnectionInfo<MongoClient>> load() {
//		
//		if (connectionInfoProperties == null 
//				|| connectionInfoProperties.getLoaders() == null 
//				|| !connectionInfoProperties.getLoaders().containsKey(getLoaderKey())
//				|| CollectionUtils.isEmpty(connectionInfoProperties.getLoaders().get(getLoaderKey()).getConnections())) {
//			return Collections.emptyList();
//		}
//
//		List<String> connectionList = connectionInfoProperties.getLoaders().get(getLoaderKey()).getConnections().values().stream().flatMap(List::stream).distinct().toList(); 
//		
//		return load(connectionList);
//	}
//
//	@Override
//	public MongoDbMongoClientConnectionConfigReader getConnectionConfigReader() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	
//
//}
