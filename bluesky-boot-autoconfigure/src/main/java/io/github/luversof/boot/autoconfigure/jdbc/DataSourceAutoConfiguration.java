package io.github.luversof.boot.autoconfigure.jdbc;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

@Configuration(value = "_blueskyBootDataSourceAutoConfiguration", proxyBeanMethods = false)
@ConditionalOnClass({ DataSource.class, EmbeddedDatabaseType.class })
@PropertySource(value = "classpath:jdbc/jdbc.properties", ignoreResourceNotFound = true)
@PropertySource(value = "classpath:jdbc/jdbc-${net-profile}.properties", ignoreResourceNotFound = true)
public class DataSourceAutoConfiguration {

	@Bean
	@Primary
	@ConfigurationProperties("datasource.default")
	public DataSourceProperties defaultDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	@Primary
	public DataSource defaultDataSource(@Qualifier("defaultDataSourceProperties") DataSourceProperties defaultDataSourceProperties) {
		return defaultDataSourceProperties.initializeDataSourceBuilder().type(SimpleDriverDataSource.class).build();
	}

}
