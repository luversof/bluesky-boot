package io.github.luversof.boot.jdbc.datasource.lookup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import io.github.luversof.boot.connectioninfo.ConnectionInfoCollector;
import io.github.luversof.boot.connectioninfo.ConnectionInfoLoader;
import io.github.luversof.boot.jdbc.datasource.context.RoutingDataSourceContextHolder;

/**
 * lookupKey에 대한 DataSource 후처리 생성을 지원
 */
public class LazyLoadRoutingDataSource<T extends DataSource> extends RoutingDataSource {
	
	private Map<String, ConnectionInfoLoader<T, ConnectionInfoCollector<T>>> connectionInfoLoaderMap;

	public LazyLoadRoutingDataSource(Map<String, ConnectionInfoLoader<T, ConnectionInfoCollector<T>>> connectionInfoLoaderMap) {
		this.connectionInfoLoaderMap = connectionInfoLoaderMap;
	}

	@Override
	protected Object determineCurrentLookupKey() {
		// lookupKey에 대해 resolvedDataSources에 있는지 확인하여 없으면 load
		var lookupKey = RoutingDataSourceContextHolder.getContext().getLookupKey();
		if (lookupKey == null) {
			return null;
		}
		
		// lookupKey가 등록되어 있는지 확인하여 없으면 lazy load
		DataSource dataSource = getResolvedDataSources().get(lookupKey);
		// TODO 조건에 캐시 관련 처리 추가 필요
		if (dataSource == null && connectionInfoLoaderMap != null) {
			
			boolean isLoaded = false;
			
			for (var connectionInfoLoader : connectionInfoLoaderMap.values()) {
				var connectionInfoCollector = connectionInfoLoader.load(List.of(lookupKey));
				Map<String, ? extends DataSource> connectionInfoMap = connectionInfoCollector.getConnectionInfoMap();
				if (connectionInfoMap.containsKey(lookupKey)) {
					var targetDataSource = connectionInfoMap.get(lookupKey);
					
					var dataSourceMap = new HashMap<Object, Object>();
					dataSourceMap.putAll(getResolvedDataSources());
					dataSourceMap.put(lookupKey, targetDataSource);
					setTargetDataSources(dataSourceMap);
					initialize();
					
					isLoaded = true;
				}
			}
			
			if (!isLoaded) {
				// TODO 캐싱 처리
			}
		}
		
		return RoutingDataSourceContextHolder.getContext().getLookupKey();
	}

}
