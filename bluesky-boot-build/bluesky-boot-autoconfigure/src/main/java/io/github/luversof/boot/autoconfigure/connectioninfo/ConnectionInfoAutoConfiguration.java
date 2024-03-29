package io.github.luversof.boot.autoconfigure.connectioninfo;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;

import com.mongodb.client.MongoClient;
import com.zaxxer.hikari.HikariDataSource;

import io.github.luversof.boot.autoconfigure.connectioninfo.mongo.MongoClientConnectionInfoCollectorDecorator;
import io.github.luversof.boot.autoconfigure.connectioninfo.mongo.PropertiesMongoClientConnectionInfoLoader;
import io.github.luversof.boot.connectioninfo.ConnectionInfoCollector;
import io.github.luversof.boot.connectioninfo.ConnectionInfoDevCheckController;
import io.github.luversof.boot.connectioninfo.ConnectionInfoLoader;
import io.github.luversof.boot.connectioninfo.ConnectionInfoLoaderProperties;
import io.github.luversof.boot.connectioninfo.datasource.MariaDbDataSourceConnectionInfoLoader;
import io.github.luversof.boot.connectioninfo.datasource.SQLServerDataSourceConnectionInfoLoader;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for ConnectionInfo support.
 * @author bluesky
 *
 */
@AutoConfiguration("blueskyBootConnectionInfoAutoConfiguration")
@EnableConfigurationProperties(ConnectionInfoLoaderProperties.class)
public class ConnectionInfoAutoConfiguration {
	
	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass({ DataSource.class, JdbcTemplate.class, HikariDataSource.class, org.mariadb.jdbc.Driver.class })
	@ConditionalOnProperty(prefix = "bluesky-boot.connection-info.loaders", name = "mariadb-datasource.enabled", havingValue = "true")
	static class MariaDbDataSourceConnectionInfoConfiguration {
		
		@Bean
		MariaDbDataSourceConnectionInfoLoader mariaDbDataSourceConnectionInfoLoader(ConnectionInfoLoaderProperties connectionInfoProperties) {
			return new MariaDbDataSourceConnectionInfoLoader(connectionInfoProperties);
		}

        @Bean
        ConnectionInfoCollector<HikariDataSource> mariaDbDataSourceConnectionInfoCollector(MariaDbDataSourceConnectionInfoLoader mariaDbDataSourceConnectionInfoLoader) {
            return mariaDbDataSourceConnectionInfoLoader.load();
        }
	}
	
	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass({ DataSource.class, JdbcTemplate.class, HikariDataSource.class, com.microsoft.sqlserver.jdbc.SQLServerDriver.class })
	@ConditionalOnProperty(prefix = "bluesky-boot.connection-info.loaders", name = "sqlserver-datasource.enabled", havingValue = "true")
	static class SQLServerDataSourceConnectionInfoConfiguration {
		
		@Bean
		SQLServerDataSourceConnectionInfoLoader sqlServerDataSourceConnectionInfoLoader(ConnectionInfoLoaderProperties connectionInfoProperties) {
			return new SQLServerDataSourceConnectionInfoLoader(connectionInfoProperties);
		}
		
		@Bean
		ConnectionInfoCollector<HikariDataSource> sqlServerDataSourceConnectionInfoCollector(SQLServerDataSourceConnectionInfoLoader sqlServerDataSourceConnectionInfoLoader) {
			return sqlServerDataSourceConnectionInfoLoader.load();
		}
	}
	
	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass(MongoClient.class)
	@ConditionalOnProperty(prefix = "bluesky-boot.connection-info.loaders", name = "properties-mongoclient.enabled", havingValue = "true")
	static class PropertiesMongoClientConnectionInfoConfiguration {
		
		@Bean
		PropertiesMongoClientConnectionInfoLoader propertiesMongoClientConnectionInfoLoader(ApplicationContext applicationContext, ConnectionInfoLoaderProperties connectionInfoProperties) {
			return new PropertiesMongoClientConnectionInfoLoader(applicationContext, connectionInfoProperties);
		}
		
		@Bean
		ConnectionInfoCollector<MongoClient> propertiesMongoClientConnectionInfoCollector(PropertiesMongoClientConnectionInfoLoader propertiesMongoClientConnectionInfoLoader) {
			return propertiesMongoClientConnectionInfoLoader.load();
		}
		
		@Bean
		MongoClientConnectionInfoCollectorDecorator mongoClientConnectionInfoCollectorDecorator(Map<String, ConnectionInfoLoader<MongoClient>> mongoClientConnectionInfoLoaderMap) {
			return new MongoClientConnectionInfoCollectorDecorator(mongoClientConnectionInfoLoaderMap);
		}
	}
	
	
	@Bean
	ConnectionInfoDevCheckController connectionInfoDevCheckController(ConnectionInfoLoaderProperties connectionInfoLoaderProperties, @Nullable Map<String, ConnectionInfoCollector<?>> connectionInfoCollectorMap) {
		return new ConnectionInfoDevCheckController(connectionInfoLoaderProperties, connectionInfoCollectorMap);
	}
	
}
