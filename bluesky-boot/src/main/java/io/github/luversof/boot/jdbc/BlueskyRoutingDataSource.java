package io.github.luversof.boot.jdbc;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class BlueskyRoutingDataSource extends AbstractRoutingDataSource {

	@Override
	protected Object determineCurrentLookupKey() {
		return BlueskyRoutingDataSourceContextHolder.getContext().getDataSourceName();
	}

}
