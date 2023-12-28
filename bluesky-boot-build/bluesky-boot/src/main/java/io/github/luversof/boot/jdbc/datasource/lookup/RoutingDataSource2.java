package io.github.luversof.boot.jdbc.datasource.lookup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import io.github.luversof.boot.connectioninfo.ConnectionInfoCollector;
import io.github.luversof.boot.connectioninfo.ConnectionInfoLoader;
import io.github.luversof.boot.jdbc.datasource.context.RoutingDataSourceContextHolder;

/**
 * lookupKey에 대한 DataSource 후처리 생성을 지원
 */
public class RoutingDataSource2 extends AbstractRoutingDataSource {
	
	private Map<String, ConnectionInfoLoader<? extends DataSource, ConnectionInfoCollector<? extends DataSource>>> connectionInfoLoaderMap;

	@Override
	protected Object determineCurrentLookupKey() {
		// lookupKey에 대해 resolvedDataSources에 있는지 확인하여 없으면 load
		var lookupKey = RoutingDataSourceContextHolder.getContext().getLookupKey();
		
		DataSource dataSource = getResolvedDataSources().get(lookupKey);
		if (dataSource == null && connectionInfoLoaderMap != null) {
			
			connectionInfoLoaderMap.forEach((name, connectionInfoLoader) -> {
				var connectionInfoCollector = connectionInfoLoader.load(List.of(lookupKey));
				Map<String, ? extends DataSource> connectionInfoMap = connectionInfoCollector.getConnectionInfoMap();
				if (connectionInfoMap.containsKey(lookupKey)) {
					var targetDataSource = connectionInfoMap.get(lookupKey);
					
					var dataSourceMap = new HashMap<Object, Object>();
					dataSourceMap.putAll(getResolvedDataSources());
					dataSourceMap.put(lookupKey, targetDataSource);
					setTargetDataSources(dataSourceMap);
					initialize();
				}
			});
		}
		
		return RoutingDataSourceContextHolder.getContext().getLookupKey();
	}

}
