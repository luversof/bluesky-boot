package io.github.luversof.boot.jdbc.datasource.controller;

import java.util.Collections;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.DelegatingDataSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.github.luversof.boot.devcheck.annotation.DevCheckController;
import io.github.luversof.boot.devcheck.annotation.DevCheckDescription;
import io.github.luversof.boot.jdbc.datasource.lookup.RoutingDataSource;

/**
 * {@link DevCheckController} for DataSource support.
 * 
 * @author bluesky
 *
 */
@DevCheckController
@RequestMapping(value = "/blueskyBoot/jdbc", produces = MediaType.APPLICATION_JSON_VALUE)
public class DataSourceDevCheckController {

	private DataSource blueskyRoutingDataSource;

	public DataSourceDevCheckController(DataSource blueskyRoutingDataSource) {
		this.blueskyRoutingDataSource = blueskyRoutingDataSource;
	}

	@DevCheckDescription("blueskyRoutingDataSourceKeySet 조회")
	@GetMapping("/blueskyRoutingDataSourceKeySet")
	Set<Object> blueskyRoutingDataSourceKeySet() {
		if (blueskyRoutingDataSource instanceof RoutingDataSource routingDataSource) {
			return routingDataSource.getResolvedDataSources().keySet();
		}
		if (blueskyRoutingDataSource instanceof DelegatingDataSource delegatingDataSource
				&& delegatingDataSource.getTargetDataSource() instanceof RoutingDataSource routingDataSource) {
			return routingDataSource.getResolvedDataSources().keySet();
		}
		return Collections.emptySet();
	}

}
