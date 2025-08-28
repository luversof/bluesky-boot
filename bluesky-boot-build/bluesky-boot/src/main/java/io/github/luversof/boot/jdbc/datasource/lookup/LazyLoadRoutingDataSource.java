package io.github.luversof.boot.jdbc.datasource.lookup;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import io.github.luversof.boot.connectioninfo.ConnectionConfig;
import io.github.luversof.boot.connectioninfo.ConnectionInfoLoader;
import io.github.luversof.boot.exception.BlueskyException;
import io.github.luversof.boot.jdbc.datasource.context.RoutingDataSourceContextHolder;

/**
 * connectionInfoLoader를 사용하여 DataSource를 lazy load 지원
 * lookupKey에 대한 DataSource 후처리 생성을 지원
 * 
 * @param <T> 대상 DataSource 타입
 */
public class LazyLoadRoutingDataSource<T extends DataSource, C extends ConnectionConfig> extends RoutingDataSource {
	
	private Map<String, ConnectionInfoLoader<T, C>> connectionInfoLoaderMap;
	
	private Map<String, ZonedDateTime> nonExistLookupKeyMap = new HashMap<>();

	public LazyLoadRoutingDataSource(Map<String, ConnectionInfoLoader<T, C>> connectionInfoLoaderMap) {
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
		if (dataSource == null && connectionInfoLoaderMap != null) {
			checkNonExistLookupKeyMap(lookupKey);
			
			boolean isLoaded = false;
			
			for (var connectionInfoLoader : connectionInfoLoaderMap.values()) {
				var connectionInfoList = connectionInfoLoader.load(List.of(lookupKey));
				for (var connectionInfo : connectionInfoList) {
					if (connectionInfo.getKey().connectionKey().equals(lookupKey)) {
						var targetDataSource = connectionInfo.getConnection();
						
						var dataSourceMap = new HashMap<Object, Object>();
						dataSourceMap.putAll(getResolvedDataSources());
						dataSourceMap.put(lookupKey, targetDataSource);
						setTargetDataSources(dataSourceMap);
						initialize();
						
						isLoaded = true;
						
					}
				}
			}
			
			if (!isLoaded) {
				nonExistLookupKeyMap.put(lookupKey, ZonedDateTime.now());
				throw new BlueskyException("NOT_EXIST_DATASOURCE_LOOKUPKEY", lookupKey);
			}
		}
		
		return RoutingDataSourceContextHolder.getContext().getLookupKey();
	}
	
	// 
	/**
	 * nonExistLookupKeyMap에 해당 키가 있고 캐시 기간내 요청인 경우 throw exception 처리
	 * @return
	 */
	private void checkNonExistLookupKeyMap(String lookupKey) {
		if(!nonExistLookupKeyMap.containsKey(lookupKey)) {
			return;
		}
		
		if (nonExistLookupKeyMap.get(lookupKey).isAfter(ZonedDateTime.now().minusHours(1))) {
			throw new BlueskyException("NOT_EXIST_DATASOURCE_LOOKUPKEY", lookupKey);
		}
	}

}
