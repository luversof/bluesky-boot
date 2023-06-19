package io.github.luversof.boot.autoconfigure.jdbc.controller;

import java.util.Collections;
import java.util.Set;

import javax.sql.DataSource;

import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.DelegatingDataSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.luversof.boot.autoconfigure.devcheck.core.annotation.DevCheckController;
import io.github.luversof.boot.autoconfigure.devcheck.core.annotation.DevCheckDescription;
import io.github.luversof.boot.jdbc.datasource.lookup.RoutingDataSource;
import lombok.AllArgsConstructor;

/**
 * {@link DevCheckController} for DataSource support.
 * @author bluesky
 *
 */
@AllArgsConstructor
@DevCheckController
@RestController
@RequestMapping(value = "${bluesky-boot.dev-check.path-prefix}/blueskyBoot/dataSource", produces = MediaType.APPLICATION_JSON_VALUE)
public class DataSourceDevCheckController {

	private DataSource blueskyRoutingDataSource;
	
	@DevCheckDescription("blueskyRoutingDataSourceKeySet 조회")
	@GetMapping("/blueskyRoutingDataSourceKeySet")
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
