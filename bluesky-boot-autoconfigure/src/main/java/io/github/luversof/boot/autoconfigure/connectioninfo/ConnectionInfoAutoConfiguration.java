package io.github.luversof.boot.autoconfigure.connectioninfo;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import io.github.luversof.boot.connectioninfo.ConnectionInfoProperties;

@AutoConfiguration("_blueskyBootConnectionInfoAutoConfiguration")
@EnableConfigurationProperties(ConnectionInfoProperties.class)
public class ConnectionInfoAutoConfiguration {
	
	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass({ DataSource.class, JdbcTemplate.class })
	@ConditionalOnProperty(prefix = "bluesky-modules.connection-info.loaders", name = "datasource-mariadb.enabled", havingValue = "true")
	public static class DataSourceConnectionInfoMariadbConfiguration {

        @Bean
        Map<String, DataSource> dataSourceConnectionInfoMariadbLoader(ConnectionInfoProperties connectionInfoProperties) {
            return new DataSourceConnectionInfoMariadbLoader(connectionInfoProperties).load();
        }
	}

}
