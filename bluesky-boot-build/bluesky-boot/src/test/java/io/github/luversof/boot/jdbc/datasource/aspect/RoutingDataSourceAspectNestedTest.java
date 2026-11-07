package io.github.luversof.boot.jdbc.datasource.aspect;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import io.github.luversof.boot.jdbc.datasource.annotation.RoutingDataSource;
import io.github.luversof.boot.jdbc.datasource.context.RoutingDataSourceContextHolder;

/** 안쪽 {@code @RoutingDataSource} 호출이 끝난 뒤 바깥 lookup key 가 유지되는지 본다. */
class RoutingDataSourceAspectNestedTest {

  private final List<Object> lookupKeys = new ArrayList<>();

  @AfterEach
  void clearContext() {
    RoutingDataSourceContextHolder.clearContext();
  }

  @Test
  void outerKeyIsRestoredAfterNestedCall() {
    var outer = proxy(new OuterService(proxy(new InnerService(lookupKeys)), lookupKeys));

    outer.call();

    assertThat(lookupKeys).containsExactly("outer", "inner", "outer");
  }

  @Test
  void filterKeyIsRestoredAfterAnnotatedCall() {
    var inner = proxy(new InnerService(lookupKeys));
    RoutingDataSourceContextHolder.setContext(() -> "filter");

    inner.call();

    assertThat(lookupKeys).containsExactly("inner");
    assertThat(RoutingDataSourceContextHolder.getContext().getLookupKey()).isEqualTo("filter");
  }

  @SuppressWarnings("unchecked")
  private static <T> T proxy(T target) {
    var factory = new AspectJProxyFactory(target);
    factory.setProxyTargetClass(true);
    factory.addAspect(new RoutingDataSourceAspect(null));
    return (T) factory.getProxy();
  }

  private static void record(List<Object> lookupKeys) {
    lookupKeys.add(RoutingDataSourceContextHolder.getContext().getLookupKey());
  }

  static class OuterService {

    private final InnerService inner;

    private final List<Object> lookupKeys;

    OuterService(InnerService inner, List<Object> lookupKeys) {
      this.inner = inner;
      this.lookupKeys = lookupKeys;
    }

    @RoutingDataSource("outer")
    public void call() {
      record(lookupKeys);
      inner.call();
      record(lookupKeys);
    }
  }

  static class InnerService {

    private final List<Object> lookupKeys;

    InnerService(List<Object> lookupKeys) {
      this.lookupKeys = lookupKeys;
    }

    @RoutingDataSource("inner")
    public void call() {
      record(lookupKeys);
    }
  }
}
