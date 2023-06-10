package io.github.luversof.boot.autoconfigure.jdbc.controller;

import java.util.Set;

import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.luversof.boot.autoconfigure.devcheck.core.annotation.DevCheckController;
import io.github.luversof.boot.autoconfigure.devcheck.core.annotation.DevCheckDescription;
import io.github.luversof.boot.jdbc.datasource.lookup.RoutingDataSource;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@DevCheckController
@RestController
@RequestMapping(value = "${bluesky-boot.dev-check.path-prefix}/blueskyBoot/dataSource", produces = MediaType.APPLICATION_JSON_VALUE)
public class DataSourceDevCheckController {

	private LazyConnectionDataSourceProxy blueskyRoutingDataSource;
	
	@DevCheckDescription("blueskyRoutingDataSourceKeySet 조회")
	@GetMapping("/blueskyRoutingDataSourceKeySet")
	public Set<Object> blueskyRoutingDataSourceKeySet() {
		return ((RoutingDataSource) blueskyRoutingDataSource.getTargetDataSource()).getResolvedDataSources().keySet();
	}
}
