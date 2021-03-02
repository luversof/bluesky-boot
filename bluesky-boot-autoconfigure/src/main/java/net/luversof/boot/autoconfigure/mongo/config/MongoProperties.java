package net.luversof.boot.autoconfigure.mongo.config;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.bson.UuidRepresentation;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;

import com.mongodb.ReadConcernLevel;

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
@ConfigurationProperties(prefix = "bluesky-modules.mongodb")
public class MongoProperties implements InitializingBean {

	private BlueskyMongoProperties defaultMongoProperties;
	
	private BlueskyConnectionPoolSettings defaultConnectionPoolSettings;
	
	private ReadConcernLevel defaultReadConcernLevel;
	
	private String defaultReadPreference;
	
	private BlueskyWriteConcern defaultWriteConcern;
	
	private Map<String, BlueskyMongoProperties> connectionMap = new HashMap<>();
	
	@Data
	@EqualsAndHashCode(callSuper = true)
	@NoArgsConstructor
	public static class BlueskyMongoProperties extends org.springframework.boot.autoconfigure.mongo.MongoProperties {
		
		/**
		 * 여러 서버로 구성된 경우 hosts를 설정하면 해당 설정으로 처리
		 */
		private String[] hosts;
			
		@Builder
		public BlueskyMongoProperties(String[] hosts, String host, Integer port, String uri, String database,
				String authenticationDatabase, String gridFsDatabase, String username, char[] password,
				String replicaSetName, Class<?> fieldNamingStrategy, UuidRepresentation uuidRepresentation,
				Boolean autoIndexCreation) {
			this.hosts = hosts;
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
	
	@Data
	public static class BlueskyConnectionPoolSettings {

		private int maxSize = 100;
        private int minSize;
        private long maxWaitTimeMS = 1_000L * 60 * 2;
        private long maxConnectionLifeTimeMS;
        private long maxConnectionIdleTimeMS;
        private long maintenanceInitialDelayMS;
        private long maintenanceFrequencyMS = TimeUnit.MILLISECONDS.convert(1, TimeUnit.MINUTES);

	}
	
	@Data
	public static class BlueskyWriteConcern {
		private String w;
	    private int wTimeoutMS;
	    private boolean journal;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		
		PropertyMapper propertyMapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
		
		for (var key : connectionMap.keySet()) {
			var blueskyMongoProperties = connectionMap.get(key);
			
			BlueskyMongoPropertiesBuilder builder = BlueskyMongoProperties.builder();
			
			propertyMapper.from(blueskyMongoProperties::getHosts).to(builder::hosts);
			propertyMapper.from(defaultMongoProperties::getHost).to(builder::host);
			propertyMapper.from(blueskyMongoProperties::getHost).to(builder::host);
			propertyMapper.from(defaultMongoProperties::getPort).to(builder::port);
			propertyMapper.from(blueskyMongoProperties::getPort).to(builder::port);
			propertyMapper.from(defaultMongoProperties::getUri).to(builder::uri);
			propertyMapper.from(blueskyMongoProperties::getUri).to(builder::uri);
			propertyMapper.from(defaultMongoProperties::getDatabase).to(builder::database);
			propertyMapper.from(blueskyMongoProperties::getDatabase).to(builder::database);
			propertyMapper.from(defaultMongoProperties::getAuthenticationDatabase).to(builder::authenticationDatabase);
			propertyMapper.from(blueskyMongoProperties::getAuthenticationDatabase).to(builder::authenticationDatabase);
			propertyMapper.from(defaultMongoProperties::getGridFsDatabase).to(builder::gridFsDatabase);
			propertyMapper.from(blueskyMongoProperties::getGridFsDatabase).to(builder::gridFsDatabase);
			propertyMapper.from(defaultMongoProperties::getUsername).to(builder::username);
			propertyMapper.from(blueskyMongoProperties::getUsername).to(builder::username);
			propertyMapper.from(defaultMongoProperties::getPassword).to(builder::password);
			propertyMapper.from(blueskyMongoProperties::getPassword).to(builder::password);
			propertyMapper.from(defaultMongoProperties::getReplicaSetName).to(builder::replicaSetName);
			propertyMapper.from(blueskyMongoProperties::getReplicaSetName).to(builder::replicaSetName);
			propertyMapper.from(defaultMongoProperties::getFieldNamingStrategy).to(builder::fieldNamingStrategy);
			propertyMapper.from(blueskyMongoProperties::getFieldNamingStrategy).to(builder::fieldNamingStrategy);
			propertyMapper.from(defaultMongoProperties::getUuidRepresentation).to(builder::uuidRepresentation);
			propertyMapper.from(blueskyMongoProperties::getUuidRepresentation).to(builder::uuidRepresentation);
			propertyMapper.from(defaultMongoProperties::isAutoIndexCreation).to(builder::autoIndexCreation);
			propertyMapper.from(blueskyMongoProperties::isAutoIndexCreation).to(builder::autoIndexCreation);
			
			connectionMap.put(key, builder.build());
			
		}
		
	}
}
