package io.github.luversof.boot.autoconfigure.connectioninfo.controller;

import java.util.Map;
import java.util.Set;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.luversof.boot.connectioninfo.ConnectionInfoCollector;
import io.github.luversof.boot.connectioninfo.ConnectionInfoLoaderProperties;
import io.github.luversof.boot.devcheck.annotation.DevCheckController;
import io.github.luversof.boot.devcheck.annotation.DevCheckDescription;
import lombok.AllArgsConstructor;

/**
 * {@link DevCheckController} for ConnectionInfo support.
 * @author bluesky
 *
 */
@AllArgsConstructor
@DevCheckController
@RestController
@RequestMapping(value = "${bluesky-boot.dev-check.path-prefix}/blueskyBoot/connectionInfo", produces = MediaType.APPLICATION_JSON_VALUE)
public class ConnectionInfoDevCheckController {
	
	private ConnectionInfoLoaderProperties connectionInfoLoaderProperties;

	private Map<String, ConnectionInfoCollector<?>> connectionInfoCollectorMap;
	
	/**
	 * Get connectionInfoLoaderProperties
	 * @return ConnectionInfoLoaderProperties
	 */
	@DevCheckDescription("connectionInfoLoaderProperties 조회")
	@GetMapping("/connectionInfoLoaderProperties")
	ConnectionInfoLoaderProperties connectionInfoLoaderProperties() {
		return connectionInfoLoaderProperties;
	}
	
	/**
	 * Get connectionInfoCollectorKeySet
	 * @return connectionInfoCollectorKeySet
	 */
	@DevCheckDescription("connectionInfoCollectorKeySet 조회")
	@GetMapping("/connectionInfoCollectorKeySet")
	Set<String> connectionInfoCollectorKeySet() {
		return connectionInfoCollectorMap.keySet();
	}
	
	/**
	 * See the full list of connectionInfoCollectorConnectionInfoMap
	 * @param beanName connectionInfoCollector beanName
	 * @return connectionInfoMapKeySet
	 */
	@DevCheckDescription("connectionInfoCollectorConnectionInfoMap 전체 목록 조회")
	@GetMapping("/connectionInfoCollectorConnectionInfoMap")
	Set<String> connectionInfoCollectorConnectionInfoMap(String beanName) {
		return connectionInfoCollectorMap.get(beanName).getConnectionInfoMap().keySet();
	}
}
