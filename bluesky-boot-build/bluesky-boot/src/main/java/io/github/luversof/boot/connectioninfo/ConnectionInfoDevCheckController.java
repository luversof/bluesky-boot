package io.github.luversof.boot.connectioninfo;

import java.util.Map;
import java.util.Set;

import org.springframework.web.bind.annotation.GetMapping;

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
public class ConnectionInfoDevCheckController {
	
	private static final String PATH_PREFIX = "/blueskyBoot/connectionInfo";
	
	private ConnectionInfoLoaderProperties connectionInfoLoaderProperties;

	private Map<String, ConnectionInfoCollector<?>> connectionInfoCollectorMap;
	
	/**
	 * Get connectionInfoLoaderProperties
	 * @return ConnectionInfoLoaderProperties
	 */
	@DevCheckDescription("connectionInfoLoaderProperties 조회")
	@GetMapping(PATH_PREFIX + "/connectionInfoLoaderProperties")
	ConnectionInfoLoaderProperties connectionInfoLoaderProperties() {
		return connectionInfoLoaderProperties;
	}
	
	/**
	 * Get connectionInfoCollectorKeySet
	 * @return connectionInfoCollectorKeySet
	 */
	@DevCheckDescription("connectionInfoCollectorKeySet 조회")
	@GetMapping(PATH_PREFIX + "/connectionInfoCollectorKeySet")
	Set<String> connectionInfoCollectorKeySet() {
		return connectionInfoCollectorMap.keySet();
	}
	
	/**
	 * See the full list of connectionInfoCollectorConnectionInfoMap
	 * @param beanName connectionInfoCollector beanName
	 * @return connectionInfoMapKeySet
	 */
	@DevCheckDescription("connectionInfoCollectorConnectionInfoMap 전체 목록 조회")
	@GetMapping(PATH_PREFIX + "/connectionInfoCollectorConnectionInfoMap")
	Set<String> connectionInfoCollectorConnectionInfoMap(String beanName) {
		return connectionInfoCollectorMap.get(beanName).getConnectionInfoMap().keySet();
	}
}
