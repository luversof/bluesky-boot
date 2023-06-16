package io.github.luversof.boot.autoconfigure.jdbc;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * Configuration properties for DataSource.
 */
@Data
@ConfigurationProperties("bluesky-modules.datasource")
public class DataSourceProperties {
	
	private boolean enabled;

	private String defaultDatasource;
}
