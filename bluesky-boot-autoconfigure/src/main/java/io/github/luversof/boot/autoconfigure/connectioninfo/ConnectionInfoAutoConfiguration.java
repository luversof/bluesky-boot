package io.github.luversof.boot.autoconfigure.connectioninfo;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import com.zaxxer.hikari.HikariDataSource;

import io.github.luversof.boot.connectioninfo.ConnectionInfoCollector;
import io.github.luversof.boot.connectioninfo.ConnectionInfoLoaderProperties;
import io.github.luversof.boot.connectioninfo.MariaDbDataSourceConnectionInfoLoader;
import io.github.luversof.boot.connectioninfo.SQLServerDataSourceConnectionInfoLoader;

@AutoConfiguration("_blueskyBootConnectionInfoAutoConfiguration")
@EnableConfigurationProperties(ConnectionInfoLoaderProperties.class)
public class ConnectionInfoAutoConfiguration {
	
	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass({ DataSource.class, JdbcTemplate.class, HikariDataSource.class, org.mariadb.jdbc.Driver.class })
	@ConditionalOnProperty(prefix = "bluesky-modules.connection-info.loaders", name = "mariadb-datasource.enabled", havingValue = "true")
	public static class MariaDbDataSourceConnectionInfoConfiguration {

        @Bean
        ConnectionInfoCollector<HikariDataSource> mariaDbDataSourceConnectionInfoLoader(ConnectionInfoLoaderProperties connectionInfoProperties) {
            return new MariaDbDataSourceConnectionInfoLoader(connectionInfoProperties).load();
        }
	}
	
	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass({ DataSource.class, JdbcTemplate.class, HikariDataSource.class, com.microsoft.sqlserver.jdbc.SQLServerDriver.class })
	@ConditionalOnProperty(prefix = "bluesky-modules.connection-info.loaders", name = "sqlserver-datasource.enabled", havingValue = "true")
	public static class SQLServerDataSourceConnectionInfoConfiguration {
		
		@Bean
		ConnectionInfoCollector<HikariDataSource> sqlServerDataSourceConnectionInfoLoader(ConnectionInfoLoaderProperties connectionInfoProperties) {
			return new SQLServerDataSourceConnectionInfoLoader(connectionInfoProperties).load();
		}
	}
	
}
