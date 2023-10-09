package io.github.luversof.boot.jdbc.datasource.lookup;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import io.github.luversof.boot.jdbc.datasource.context.RoutingDataSourceContextHolder;

public class RoutingDataSource extends AbstractRoutingDataSource {

	@Override
	protected Object determineCurrentLookupKey() {
		return RoutingDataSourceContextHolder.getContext().getLookupKey();
	}

}
