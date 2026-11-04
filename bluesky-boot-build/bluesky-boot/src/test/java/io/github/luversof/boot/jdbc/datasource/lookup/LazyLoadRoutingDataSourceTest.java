package io.github.luversof.boot.jdbc.datasource.lookup;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import io.github.luversof.boot.connectioninfo.ConnectionConfig;
import io.github.luversof.boot.connectioninfo.ConnectionInfo;
import io.github.luversof.boot.connectioninfo.ConnectionInfoKey;
import io.github.luversof.boot.connectioninfo.ConnectionInfoLoader;
import io.github.luversof.boot.connectioninfo.ConnectionInfoReader;
import io.github.luversof.boot.exception.BlueskyException;
import io.github.luversof.boot.jdbc.datasource.context.RoutingDataSourceContextHolder;

/** 기동 때 받은 키는 그대로, 처음 보는 키는 한 번만 불러오고, 없는 키는 캐시 기간 동안 다시 부르지 않는다. */
class LazyLoadRoutingDataSourceTest {

  private final DataSource startup = mock(DataSource.class);
  private final DataSource lazy = mock(DataSource.class);
  private final FakeLoader loader = new FakeLoader(Map.of("lazy", lazy));
  private LazyLoadRoutingDataSource<DataSource, ConnectionConfig> routingDataSource;

  @BeforeEach
  void setUp() {
    routingDataSource = new LazyLoadRoutingDataSource<>(Map.of("fake", loader));
    routingDataSource.setTargetDataSources(Map.of("startup", startup));
    routingDataSource.setDefaultTargetDataSource(startup);
    routingDataSource.setLenientFallback(false);
    routingDataSource.afterPropertiesSet();
  }

  @AfterEach
  void tearDown() {
    RoutingDataSourceContextHolder.clearContext();
  }

  @Test
  void startupKeyDoesNotLoad() {
    assertThat(target("startup")).isSameAs(startup);
    assertThat(target(null)).isSameAs(startup);
    assertThat(loader.calls).hasValue(0);
  }

  @Test
  void newKeyLoadsOnceUnderConcurrency() throws Exception {
    int threads = 16;
    var ready = new CountDownLatch(threads);
    var start = new CountDownLatch(1);
    var pool = Executors.newFixedThreadPool(threads);
    try {
      List<Future<DataSource>> results = new ArrayList<>();
      for (int i = 0; i < threads; i++) {
        Callable<DataSource> task =
            () -> {
              ready.countDown();
              start.await();
              return target("lazy");
            };
        results.add(pool.submit(task));
      }
      ready.await();
      start.countDown();
      for (var result : results) {
        assertThat(result.get()).isSameAs(lazy);
      }
    } finally {
      pool.shutdownNow();
    }
    assertThat(loader.calls).hasValue(1);
    assertThat(routingDataSource.getResolvedDataSources()).containsKeys("startup", "lazy");
  }

  @Test
  void unknownKeyIsNotRetriedWithinCacheWindow() {
    assertThatThrownBy(() -> target("missing")).isInstanceOf(BlueskyException.class);
    assertThatThrownBy(() -> target("missing")).isInstanceOf(BlueskyException.class);
    assertThat(loader.calls).hasValue(1);
    assertThat(routingDataSource.getResolvedDataSources()).doesNotContainKey("missing");
  }

  private DataSource target(String lookupKey) {
    RoutingDataSourceContextHolder.setContext(() -> lookupKey);
    return routingDataSource.determineTargetDataSource();
  }

  static class FakeLoader implements ConnectionInfoLoader<DataSource, ConnectionConfig> {

    private final Map<String, DataSource> known;
    private final AtomicInteger calls = new AtomicInteger();

    FakeLoader(Map<String, DataSource> known) {
      this.known = known;
    }

    @Override
    public String getLoaderKey() {
      return "fake";
    }

    @Override
    public List<ConnectionInfoReader<ConnectionConfig>> getConnectionInfoReaderList() {
      return List.of();
    }

    @Override
    public List<ConnectionInfo<DataSource>> load() {
      return List.of();
    }

    @Override
    public List<ConnectionInfo<DataSource>> load(List<String> connectionList) {
      calls.incrementAndGet();
      try {
        // 다른 스레드가 같은 키로 겹쳐 들어올 시간을 준다
        Thread.sleep(50);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
      return connectionList.stream()
          .filter(known::containsKey)
          .map(key -> new ConnectionInfo<>(new ConnectionInfoKey("fake", key), known.get(key)))
          .toList();
    }
  }
}
