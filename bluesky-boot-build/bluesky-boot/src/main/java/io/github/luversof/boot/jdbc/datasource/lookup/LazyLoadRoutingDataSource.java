package io.github.luversof.boot.jdbc.datasource.lookup;

import java.time.Instant;
import java.util.Collections;
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
 * <p>기동 때 받은 DataSource 는 부모의 맵에 두고 다시 만들지 않는다. 늦게 붙는 DataSource 는 따로 {@link ConcurrentHashMap} 에
 * 키별로 붙인다 - 예전처럼 부모 맵을 {@code initialize()} 로 다시 만들면, 그 사이 다른 스레드가 채워지는 중인 맵을 읽어 이미 있는 키로도 "Cannot
 * determine target DataSource" 가 났다.
 *
 * @param <T> 대상 DataSource 타입
 */
public class LazyLoadRoutingDataSource<T extends DataSource, C extends ConnectionConfig>
    extends RoutingDataSource {

  private final Map<String, ConnectionInfoLoader<T, C>> connectionInfoLoaderMap;

  private final Map<String, DataSource> loadedDataSources = new ConcurrentHashMap<>();

  private static final int MAX_NON_EXIST_LOOKUP_KEYS = 10_000;

  private final Map<String, Instant> nonExistLookupKeyMap = new ConcurrentHashMap<>();

  public LazyLoadRoutingDataSource(
      Map<String, ConnectionInfoLoader<T, C>> connectionInfoLoaderMap) {
    this.connectionInfoLoaderMap = connectionInfoLoaderMap;
  }

  @Override
  protected DataSource determineTargetDataSource() {
    var lookupKey = RoutingDataSourceContextHolder.getContext().getLookupKey();
    if (lookupKey == null
        || connectionInfoLoaderMap == null
        || super.getResolvedDataSources().containsKey(lookupKey)) {
      return super.determineTargetDataSource();
    }
    // 같은 키는 한 스레드만 만든다(풀이 둘 생겨 하나가 열린 채 남지 않게), 다른 키는 기다리지 않는다.
    return loadedDataSources.computeIfAbsent(lookupKey, this::loadDataSource);
  }

  /** 기동 때 받은 DataSource 와 늦게 붙은 DataSource 를 합쳐 돌려준다. */
  @Override
  public Map<Object, DataSource> getResolvedDataSources() {
    var dataSources = new HashMap<Object, DataSource>(super.getResolvedDataSources());
    dataSources.putAll(loadedDataSources);
    return Collections.unmodifiableMap(dataSources);
  }

  private DataSource loadDataSource(String lookupKey) {
    checkNonExistLookupKeyMap(lookupKey);

    for (var connectionInfoLoader : connectionInfoLoaderMap.values()) {
      for (var connectionInfo : connectionInfoLoader.load(List.of(lookupKey))) {
        if (connectionInfo.getKey().connectionKey().equals(lookupKey)) {
          return connectionInfo.getConnection();
        }
      }
    }

    // 요청 값에서 온 키는 끝없이 다양할 수 있어 한도를 넘으면 비운다(비워도 다시 한 번 조회할 뿐이다)
    if (nonExistLookupKeyMap.size() >= MAX_NON_EXIST_LOOKUP_KEYS) {
      nonExistLookupKeyMap.clear();
    }
    nonExistLookupKeyMap.put(lookupKey, Instant.now());
    throw new BlueskyException("NOT_EXIST_DATASOURCE_LOOKUPKEY", lookupKey);
  }

  /**
   * nonExistLookupKeyMap에 해당 키가 있고 캐시 기간내 요청인 경우 throw exception 처리.
   *
   * @param lookupKey 조회하려는 lookupKey
   */
  private void checkNonExistLookupKeyMap(String lookupKey) {
    var failedAt = nonExistLookupKeyMap.get(lookupKey);
    if (failedAt != null && failedAt.isAfter(Instant.now().minusSeconds(3600))) {
      throw new BlueskyException("NOT_EXIST_DATASOURCE_LOOKUPKEY", lookupKey);
    }
  }
}
