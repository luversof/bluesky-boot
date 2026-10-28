package io.github.luversof.boot.jdbc.datasource.lookup;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.sql.DataSource;

import io.github.luversof.boot.connectioninfo.ConnectionConfig;
import io.github.luversof.boot.connectioninfo.ConnectionInfoLoader;
import io.github.luversof.boot.exception.BlueskyException;
import io.github.luversof.boot.jdbc.datasource.context.RoutingDataSourceContextHolder;

/**
 * connectionInfoLoader를 사용하여 DataSource를 lazy load 지원 lookupKey에 대한 DataSource 후처리 생성을 지원
 *
 * @param <T> 대상 DataSource 타입
 */
public class LazyLoadRoutingDataSource<T extends DataSource, C extends ConnectionConfig>
    extends RoutingDataSource {

  private Map<String, ConnectionInfoLoader<T, C>> connectionInfoLoaderMap;

  private final Map<String, Instant> nonExistLookupKeyMap = new ConcurrentHashMap<>();

  public LazyLoadRoutingDataSource(
      Map<String, ConnectionInfoLoader<T, C>> connectionInfoLoaderMap) {
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
      loadDataSource(lookupKey);
    }

    return RoutingDataSourceContextHolder.getContext().getLookupKey();
  }

  /**
   * 처음 보는 lookupKey 의 DataSource 를 만들어 붙인다. 한 번에 한 스레드만 - 예전엔 두 요청이 같은 새 키로 동시에 들어오면 풀을 두 개 만들어 하나가
   * 참조 없이 열린 채 남았다.
   */
  private synchronized void loadDataSource(String lookupKey) {
    // 기다리는 동안 다른 스레드가 이미 붙였을 수 있다
    if (getResolvedDataSources().get(lookupKey) != null) {
      return;
    }
    checkNonExistLookupKeyMap(lookupKey);

    boolean isLoaded = false;

    for (var connectionInfoLoader : connectionInfoLoaderMap.values()) {
      var connectionInfoList = connectionInfoLoader.load(List.of(lookupKey));
      for (var connectionInfo : connectionInfoList) {
        if (connectionInfo.getKey().connectionKey().equals(lookupKey)) {
          var targetDataSource = connectionInfo.getConnection();

          var dataSourceMap = new HashMap<>();
          dataSourceMap.putAll(getResolvedDataSources());
          dataSourceMap.put(lookupKey, targetDataSource);
          setTargetDataSources(dataSourceMap);
          initialize();

          isLoaded = true;
        }
      }
    }

    if (!isLoaded) {
      nonExistLookupKeyMap.put(lookupKey, Instant.now());
      throw new BlueskyException("NOT_EXIST_DATASOURCE_LOOKUPKEY", lookupKey);
    }
  }

  /**
   * nonExistLookupKeyMap에 해당 키가 있고 캐시 기간내 요청인 경우 throw exception 처리.
   *
   * @param lookupKey 조회하려는 lookupKey
   */
  private void checkNonExistLookupKeyMap(String lookupKey) {
    if (!nonExistLookupKeyMap.containsKey(lookupKey)) {
      return;
    }

    if (nonExistLookupKeyMap.get(lookupKey).isAfter(Instant.now().minusSeconds(3600))) {
      throw new BlueskyException("NOT_EXIST_DATASOURCE_LOOKUPKEY", lookupKey);
    }
  }
}
