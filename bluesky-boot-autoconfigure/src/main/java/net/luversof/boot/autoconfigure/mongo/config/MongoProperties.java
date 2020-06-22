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
import lombok.NoArgsConstructor;
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
	@NoArgsConstructor
	public static class BlueskyMongoProperties extends org.springframework.boot.autoconfigure.mongo.MongoProperties {
			
		@Builder
		public BlueskyMongoProperties(String host, Integer port, String uri, String database,
				String authenticationDatabase, String gridFsDatabase, String username, char[] password,
				String replicaSetName, Class<?> fieldNamingStrategy, UuidRepresentation uuidRepresentation,
				Boolean autoIndexCreation) {
			setHost(host);
			setPort(port);
			setUri(uri);
			setDatabase(database);
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
			
			propertyMapper.from(defaultProperties::getHost).to(builder::host);
			propertyMapper.from(blueskyMongoProperties::getHost).to(builder::host);
			propertyMapper.from(defaultProperties::getPort).to(builder::port);
			propertyMapper.from(blueskyMongoProperties::getPort).to(builder::port);
			propertyMapper.from(defaultProperties::getUri).to(builder::uri);
			propertyMapper.from(blueskyMongoProperties::getUri).to(builder::uri);
			propertyMapper.from(defaultProperties::getDatabase).to(builder::database);
			propertyMapper.from(blueskyMongoProperties::getDatabase).to(builder::database);
			propertyMapper.from(defaultProperties::getAuthenticationDatabase).to(builder::authenticationDatabase);
			propertyMapper.from(blueskyMongoProperties::getAuthenticationDatabase).to(builder::authenticationDatabase);
			propertyMapper.from(defaultProperties::getGridFsDatabase).to(builder::gridFsDatabase);
			propertyMapper.from(blueskyMongoProperties::getGridFsDatabase).to(builder::gridFsDatabase);
			propertyMapper.from(defaultProperties::getUsername).to(builder::username);
			propertyMapper.from(blueskyMongoProperties::getUsername).to(builder::username);
			propertyMapper.from(defaultProperties::getPassword).to(builder::password);
			propertyMapper.from(blueskyMongoProperties::getPassword).to(builder::password);
			propertyMapper.from(defaultProperties::getReplicaSetName).to(builder::replicaSetName);
			propertyMapper.from(blueskyMongoProperties::getReplicaSetName).to(builder::replicaSetName);
			propertyMapper.from(defaultProperties::getFieldNamingStrategy).to(builder::fieldNamingStrategy);
			propertyMapper.from(blueskyMongoProperties::getFieldNamingStrategy).to(builder::fieldNamingStrategy);
			propertyMapper.from(defaultProperties::getUuidRepresentation).to(builder::uuidRepresentation);
			propertyMapper.from(blueskyMongoProperties::getUuidRepresentation).to(builder::uuidRepresentation);
			propertyMapper.from(defaultProperties::isAutoIndexCreation).to(builder::autoIndexCreation);
			propertyMapper.from(blueskyMongoProperties::isAutoIndexCreation).to(builder::autoIndexCreation);
			
			connectionMap.put(key, builder.build());
			
		}
		
	}
}
