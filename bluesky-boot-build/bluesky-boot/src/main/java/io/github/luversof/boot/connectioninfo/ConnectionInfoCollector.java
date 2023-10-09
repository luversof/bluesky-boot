package io.github.luversof.boot.connectioninfo;

import java.util.Map;

/**
 * ConnectionInfoLoader를 통해 수집한 ConnectionInfo를 담고 있는 객체
 * @author bluesky
 *
 * @param <T>
 */
@FunctionalInterface
public interface ConnectionInfoCollector<T> {
	
	Map<String, T> getConnectionInfoMap();

}
