package io.github.luversof.boot.jdbc.datasource.lookup;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import io.github.luversof.boot.jdbc.datasource.BlueskyRoutingDataSourceContextHolder;

public class BlueskyRoutingDataSource extends AbstractRoutingDataSource {

	@Override
	protected Object determineCurrentLookupKey() {
		return BlueskyRoutingDataSourceContextHolder.getContext().getDataSourceName();
	}

}
