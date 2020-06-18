package net.luversof.boot.autoconfigure.mongo.config;

import java.util.HashMap;
import java.util.Map;

import org.bson.UuidRepresentation;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.luversof.boot.autoconfigure.mongo.config.MongoProperties.BlueskyMongoProperties.BlueskyMongoPropertiesBuilder;

/**
 * 굳이 module 구성 할 이유가 없어보이는데?
 * @author bluesky
 *
 */
@Data
@ConfigurationProperties(prefix = "bluesky-modules.mongo")
public class MongoProperties implements InitializingBean {

	private BlueskyMongoProperties defaultProperties;
	
	private Map<String, BlueskyMongoProperties> connectionMap = new HashMap<>();

	@Data
	@EqualsAndHashCode(callSuper = true)
	public static class BlueskyMongoProperties extends org.springframework.boot.autoconfigure.mongo.MongoProperties {
			
		@Builder
		public BlueskyMongoProperties(String host, Integer port, String uri, String database,
				String authenticationDatabase, String gridFsDatabase, String username, char[] password,
				String replicaSetName, Class<?> fieldNamingStrategy, UuidRepresentation uuidRepresentation,
				Boolean autoIndexCreation) {
			setHost(host);
			setPort(port);
			setUri(uri);
			setDatabase(gridFsDatabase);
			setAuthenticationDatabase(authenticationDatabase);
			setGridFsDatabase(gridFsDatabase);
			setUsername(username);
			setPassword(password);
			setReplicaSetName(replicaSetName);
			setFieldNamingStrategy(fieldNamingStrategy);
			setUuidRepresentation(uuidRepresentation == null ? UuidRepresentation.JAVA_LEGACY : uuidRepresentation);
			setAutoIndexCreation(autoIndexCreation);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		
		PropertyMapper propertyMapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
		
		for (var key : connectionMap.keySet()) {
			var blueskyMongoProperties = connectionMap.get(key);
			
			BlueskyMongoPropertiesBuilder builder = BlueskyMongoProperties.builder();
			
			propertyMapper.from(blueskyMongoProperties::getHost).to(builder::host);
			propertyMapper.from(blueskyMongoProperties::getPort).to(builder::port);
			propertyMapper.from(blueskyMongoProperties::getUri).to(builder::uri);
			propertyMapper.from(blueskyMongoProperties::getGridFsDatabase).to(builder::gridFsDatabase);
			propertyMapper.from(blueskyMongoProperties::getUsername).to(builder::username);
			propertyMapper.from(blueskyMongoProperties::getPassword).to(builder::password);
			propertyMapper.from(blueskyMongoProperties::getReplicaSetName).to(builder::replicaSetName);
			propertyMapper.from(blueskyMongoProperties::getFieldNamingStrategy).to(builder::fieldNamingStrategy);
			propertyMapper.from(blueskyMongoProperties::getUuidRepresentation).to(builder::uuidRepresentation);
			propertyMapper.from(blueskyMongoProperties::isAutoIndexCreation).to(builder::autoIndexCreation);
			
			connectionMap.put(key, builder.build());
			
		}
		
	}
}
