package io.github.luversof.boot.autoconfigure.mongo.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.mongo.MongoClientSettingsBuilderCustomizer;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoCredential;
import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.connection.ClusterConnectionMode;

import io.github.luversof.boot.autoconfigure.mongo.config.MongoProperties.BlueskyMongoProperties;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MongoUtil {
	

	public static MongoClientSettings getDefaultMongoClientSettings(MongoProperties mongoProperties) {

		var builder = MongoClientSettings.builder();

		var connectionPoolSettings = mongoProperties.getDefaultConnectionPoolSettings();
		if (connectionPoolSettings != null) {
			builder.applyToConnectionPoolSettings(b -> b.maxSize(connectionPoolSettings.getMaxSize())
					.minSize(connectionPoolSettings.getMinSize())
					.maxWaitTime(connectionPoolSettings.getMaxWaitTimeMS(), TimeUnit.MILLISECONDS)
					.maxConnectionLifeTime(connectionPoolSettings.getMaxConnectionLifeTimeMS(), TimeUnit.MILLISECONDS)
					.maxConnectionIdleTime(connectionPoolSettings.getMaxConnectionIdleTimeMS(), TimeUnit.MILLISECONDS)
					.maintenanceInitialDelay(connectionPoolSettings.getMaintenanceInitialDelayMS(),
							TimeUnit.MILLISECONDS)
					.maintenanceFrequency(connectionPoolSettings.getMaintenanceFrequencyMS(), TimeUnit.MILLISECONDS));
		}

		if (mongoProperties.getDefaultReadConcernLevel() != null) {
			builder.readConcern(new ReadConcern(mongoProperties.getDefaultReadConcernLevel()));
		}

		if (mongoProperties.getDefaultReadPreference() != null) {
			builder.readPreference(ReadPreference.valueOf(mongoProperties.getDefaultReadPreference()));
		}

		var writeConcern = mongoProperties.getDefaultWriteConcern();
		if (writeConcern != null) {
			builder.writeConcern(WriteConcern.valueOf(writeConcern.getW())
					.withWTimeout(writeConcern.getWTimeoutMS(), TimeUnit.MILLISECONDS)
					.withJournal(writeConcern.isJournal()));
		}

		return builder.build();
	}
	
	public static MongoClient getMongoClient(BlueskyMongoProperties blueskyMongoProperties, ObjectProvider<MongoClientSettingsBuilderCustomizer> builderCustomizers, MongoClientSettings settings) {
		var builder = settings != null ? MongoClientSettings.builder(settings) : MongoClientSettings.builder();
		builder.uuidRepresentation(blueskyMongoProperties.getUuidRepresentation());
		if (blueskyMongoProperties.getUri() != null) {
			builder.applyConnectionString(new ConnectionString(blueskyMongoProperties.getUri()));
		} else if (blueskyMongoProperties.getHosts() != null && blueskyMongoProperties.getHosts().length > 0) {
			var serverAddressList = new ArrayList<ServerAddress>();
			for (String host : blueskyMongoProperties.getHosts()) {
				if (host.contains(":")) {
					String[] sa = host.split(":");
					serverAddressList.add(new ServerAddress(sa[0], Integer.parseInt(sa[1])));
				} else {
					serverAddressList.add(new ServerAddress(host));
				}
			}
			builder.applyToClusterSettings(cluster -> cluster.hosts(serverAddressList).mode(ClusterConnectionMode.MULTIPLE));
		} else if (blueskyMongoProperties.getHost() != null && blueskyMongoProperties.getPort() != null) {
			builder.applyToClusterSettings(cluster -> cluster.hosts(Collections.singletonList(new ServerAddress(blueskyMongoProperties.getHost(), blueskyMongoProperties.getPort()))));
		}
		
		if (blueskyMongoProperties.getUsername() != null && blueskyMongoProperties.getPassword() != null) {
			String database = (blueskyMongoProperties.getAuthenticationDatabase() != null) ? blueskyMongoProperties.getAuthenticationDatabase() : blueskyMongoProperties.getMongoClientDatabase();
			builder.credential((MongoCredential.createCredential(blueskyMongoProperties.getUsername(), database, blueskyMongoProperties.getPassword())));
		}
		
		if (blueskyMongoProperties.getReplicaSetName() != null) {
			builder.applyToClusterSettings(cluster -> cluster.requiredReplicaSetName(blueskyMongoProperties.getReplicaSetName()));
		}

		for (MongoClientSettingsBuilderCustomizer customizer : builderCustomizers.orderedStream().toList()) {
			customizer.customize(builder);
		}
		
		return MongoClients.create(builder.build());
	}
}
