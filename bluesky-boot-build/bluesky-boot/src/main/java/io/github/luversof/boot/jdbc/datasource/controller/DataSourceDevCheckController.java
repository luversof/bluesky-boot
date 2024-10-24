package io.github.luversof.boot.jdbc.datasource.controller;

import java.util.Collections;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DelegatingDataSource;
import org.springframework.web.bind.annotation.GetMapping;

import io.github.luversof.boot.devcheck.annotation.DevCheckController;
import io.github.luversof.boot.devcheck.annotation.DevCheckDescription;
import io.github.luversof.boot.jdbc.datasource.lookup.RoutingDataSource;
import lombok.AllArgsConstructor;

/**
 * {@link DevCheckController} for DataSource support.
 * @author bluesky
 *
 */
@AllArgsConstructor
@DevCheckController
public class DataSourceDevCheckController {
	
	private static final String PATH_PREFIX = "/blueskyBoot/jdbc";	// NOSONAR java:S1075

	private DataSource blueskyRoutingDataSource;
	
	@DevCheckDescription("blueskyRoutingDataSourceKeySet 조회")
	@GetMapping(PATH_PREFIX + "/blueskyRoutingDataSourceKeySet")
	Set<Object> blueskyRoutingDataSourceKeySet() {
		if (blueskyRoutingDataSource instanceof RoutingDataSource routingDataSource) {
			return routingDataSource.getResolvedDataSources().keySet();
		}
		if (blueskyRoutingDataSource instanceof DelegatingDataSource delegatingDataSource && delegatingDataSource.getTargetDataSource() instanceof RoutingDataSource routingDataSource) {
			return routingDataSource.getResolvedDataSources().keySet();
		} 
		return Collections.emptySet();
	}

}
