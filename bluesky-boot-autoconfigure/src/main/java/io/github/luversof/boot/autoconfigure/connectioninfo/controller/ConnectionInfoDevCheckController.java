package io.github.luversof.boot.autoconfigure.connectioninfo.controller;

import java.util.Map;
import java.util.Set;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.luversof.boot.autoconfigure.devcheck.core.annotation.DevCheckController;
import io.github.luversof.boot.autoconfigure.devcheck.core.annotation.DevCheckDescription;
import io.github.luversof.boot.connectioninfo.ConnectionInfoCollector;
import io.github.luversof.boot.connectioninfo.ConnectionInfoLoaderProperties;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@DevCheckController
@RestController
@RequestMapping(value = "${bluesky-boot.dev-check.path-prefix}/blueskyBoot/connectionInfo", produces = MediaType.APPLICATION_JSON_VALUE)
public class ConnectionInfoDevCheckController {
	
	private ConnectionInfoLoaderProperties connectionInfoLoaderProperties;

	private Map<String, ConnectionInfoCollector<?>> connectionInfoCollectorMap;
	
	@DevCheckDescription("connectionInfoLoaderProperties 조회")
	@GetMapping("/connectionInfoLoaderProperties")
	public ConnectionInfoLoaderProperties connectionInfoLoaderProperties() {
		return connectionInfoLoaderProperties;
	}
	
	@DevCheckDescription("connectionInfoCollectorKeySet 조회")
	@GetMapping("/connectionInfoCollectorKeySet")
	public Set<String> connectionInfoCollectorKeySet() {
		return connectionInfoCollectorMap.keySet();
	}
	
	@DevCheckDescription("connectionInfoCollectorConnectionInfoMap 전체 목록 조회")
	@GetMapping("/connectionInfoCollectorConnectionInfoMap")
	public Set<String> connectionInfoCollectorConnectionInfoMap(String beanName) {
		return connectionInfoCollectorMap.get(beanName).getConnectionInfoMap().keySet();
	}
	
//	@DevCheckDescription("connectionInfoCollectorConnectionInfo 조회")
//	@GetMapping("/connectionInfoCollectorConnectionInfo")
//	public Object connectionInfoCollectorConnectionInfo(String beanName, String connectionInfoName) {
//		return connectionInfoCollectorMap.get(beanName).getConnectionInfoMap().get(connectionInfoName);
//	}
}
