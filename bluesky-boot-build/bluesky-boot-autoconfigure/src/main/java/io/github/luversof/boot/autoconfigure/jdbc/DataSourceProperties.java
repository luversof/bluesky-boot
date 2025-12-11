package io.github.luversof.boot.autoconfigure.jdbc;

import java.util.Objects;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for DataSource.
 */
@ConfigurationProperties("bluesky-boot.datasource")
public class DataSourceProperties {

	private boolean enabled;

	private String defaultDatasource;

	private boolean useLazyLoadRoutingDataSource;

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getDefaultDatasource() {
		return defaultDatasource;
	}

	public void setDefaultDatasource(String defaultDatasource) {
		this.defaultDatasource = defaultDatasource;
	}

	public boolean isUseLazyLoadRoutingDataSource() {
		return useLazyLoadRoutingDataSource;
	}

	public void setUseLazyLoadRoutingDataSource(boolean useLazyLoadRoutingDataSource) {
		this.useLazyLoadRoutingDataSource = useLazyLoadRoutingDataSource;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		DataSourceProperties that = (DataSourceProperties) o;
		return enabled == that.enabled && useLazyLoadRoutingDataSource == that.useLazyLoadRoutingDataSource
				&& Objects.equals(defaultDatasource, that.defaultDatasource);
	}

	@Override
	public int hashCode() {
		return Objects.hash(enabled, defaultDatasource, useLazyLoadRoutingDataSource);
	}

	@Override
	public String toString() {
		return "DataSourceProperties{" +
				"enabled=" + enabled +
				", defaultDatasource='" + defaultDatasource + '\'' +
				", useLazyLoadRoutingDataSource=" + useLazyLoadRoutingDataSource +
				'}';
	}
}
