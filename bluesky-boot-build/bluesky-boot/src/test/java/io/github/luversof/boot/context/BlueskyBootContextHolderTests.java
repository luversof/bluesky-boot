package io.github.luversof.boot.context;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.ConfigurationWarningsApplicationContextInitializer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import io.github.luversof.boot.core.CoreBaseProperties;
import io.github.luversof.boot.core.CoreModuleProperties;
import io.github.luversof.boot.core.CoreProperties;

class BlueskyBootContextHolderTests {

  private AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();

  @AfterEach
  void cleanUp() {
    this.context.close();
  }

  private void load() {
    new ConfigurationWarningsApplicationContextInitializer().initialize(context);
    new BlueskyApplicationContextInitializer().initialize(context);
    context.register(TestConfiguration.class);
    context.refresh();
  }

  @Test
  void getBlueskyContext() {
    load();
    var blueskyContext = BlueskyContextHolder.getContext();
    assertThat(blueskyContext).isNotNull();
  }

  @Test
  @Disabled("ContextHolder 테스트는 전체 테스트 수행 시 code coverage 처리가 되지 않음")
  void getBlueskyContext2() {
    System.setProperty(BlueskyContextHolder.SYSTEM_PROPERTY, BlueskyContextHolder.MODE_GLOBAL);
    load();
    var blueskyContext = BlueskyContextHolder.getContext();
    assertThat(blueskyContext).isNotNull();
  }

  @Test
  void getBlueskyBootContext() {
    load();
    var blueskyContext = BlueskyBootContextHolder.getContext();
    assertThat(blueskyContext).isNotNull();
  }

  /**
   * 모듈 properties 는 같은 인스턴스를 돌려주고, 제네릭 타입 bean 조회는 context 마다 한 번만 한다.
   *
   * <p>실측 2026-09-23: 요청 파라미터를 변환할 때마다 로케일을 물으며 이 조회가 돌아 api-stock 한 요청의 표본 48% 를 차지했다.
   */
  @Test
  void 모듈_properties_bean_조회는_한_번만_한다() {
    load();
    var spy = org.mockito.Mockito.spy(context);
    ApplicationContextUtil.setApplicationContext(spy);
    try {
      var first = BlueskyContextHolder.getCoreProperties();
      var second = BlueskyContextHolder.getCoreProperties();
      var third = BlueskyContextHolder.getCoreProperties();

      assertThat(first).isNotNull().isSameAs(second).isSameAs(third);
      assertThat(first).isSameAs(context.getBean(CoreModuleProperties.class).getParent());
      org.mockito.Mockito.verify(spy, org.mockito.Mockito.times(1))
          .getBeanNamesForType(
              org.mockito.ArgumentMatchers.any(org.springframework.core.ResolvableType.class));
    } finally {
      ApplicationContextUtil.setApplicationContext(context);
    }
  }

  /** 기억은 context 마다 따로다 - 시험처럼 context 를 새로 띄우면 그 context 에서 새로 조회한다(한 번). */
  @Test
  void 다른_context_는_따로_조회한다() {
    load();
    var type =
        org.springframework.core.ResolvableType.forClassWithGenerics(
            io.github.luversof.boot.core.BlueskyModuleProperties.class, CoreProperties.class);
    var names = BlueskyContextHolder.orderedBeanNames(context, type);
    assertThat(names).isNotNull().isNotEmpty();

    var other = new AnnotationConfigApplicationContext();
    try {
      new ConfigurationWarningsApplicationContextInitializer().initialize(other);
      new BlueskyApplicationContextInitializer().initialize(other);
      other.register(TestConfiguration.class);
      other.refresh();
      var spyOther = org.mockito.Mockito.spy(other);
      assertThat(BlueskyContextHolder.orderedBeanNames(spyOther, type)).isEqualTo(names);
      assertThat(BlueskyContextHolder.orderedBeanNames(spyOther, type)).isEqualTo(names);
      org.mockito.Mockito.verify(spyOther, org.mockito.Mockito.times(1))
          .getBeanNamesForType(
              org.mockito.ArgumentMatchers.any(org.springframework.core.ResolvableType.class));
    } finally {
      other.close();
      ApplicationContextUtil.setApplicationContext(context);
    }
  }

  /** 모듈 properties bean 이 여럿이면 예전과 같은 순서(orderedStream)의 첫 번째를 쓴다 - 이름을 기억해도 고르는 bean 은 같다. */
  @Test
  void 여럿이면_예전과_같은_순서의_첫_번째를_쓴다() {
    new ConfigurationWarningsApplicationContextInitializer().initialize(context);
    new BlueskyApplicationContextInitializer().initialize(context);
    context.register(TestConfiguration.class, EarlierModuleConfiguration.class);
    context.refresh();
    ApplicationContextUtil.setApplicationContext(context);

    var type =
        org.springframework.core.ResolvableType.forClassWithGenerics(
            io.github.luversof.boot.core.BlueskyModuleProperties.class, CoreProperties.class);
    org.springframework.beans.factory.ObjectProvider<
            io.github.luversof.boot.core.BlueskyModuleProperties<CoreProperties>>
        provider = context.getBeanProvider(type);
    var legacyFirst = provider.orderedStream().toList().get(0).getParent();

    assertThat(legacyFirst)
        .as("앞 순서 bean 이 제 parent 를 갖는다 - 이게 아니면 이 시험은 헛돈다")
        .isSameAs(EARLIER_PARENT);
    assertThat(BlueskyContextHolder.getCoreProperties()).isSameAs(legacyFirst);
    assertThat(BlueskyContextHolder.getCoreProperties()).as("기억한 뒤에도").isSameAs(legacyFirst);
  }

  /** 싱글톤이 아니면 이름을 인스턴스로 되짚을 수 없다 - 기억하지 않고 매번 null(호출하는 쪽이 예전 방식으로)을 돌려준다. */
  @Test
  void 싱글톤이_아니면_기억하지_않는다() {
    new ConfigurationWarningsApplicationContextInitializer().initialize(context);
    new BlueskyApplicationContextInitializer().initialize(context);
    context.register(TestConfiguration.class, PrototypeConfiguration.class);
    context.refresh();

    var type = org.springframework.core.ResolvableType.forClass(StringBuilder.class);
    assertThat(BlueskyContextHolder.orderedBeanNames(context, type)).isNull();
    assertThat(BlueskyContextHolder.orderedBeanNames(context, type)).as("두 번째도").isNull();
  }

  /**
   * 한 번 기억한 context 의 조회는 전역 락(동기화 맵)을 기다리지 않는다 - 다른 스레드가 그 락을 쥐고 있어도 바로 끝나야 한다.
   *
   * <p>사용자 질문 2026-09-28: 대량 요청에서 모든 요청 스레드가 락 하나를 지나가는 구조를 빼자. 실측으로는 경합 0 이었지만(동시 15.6), 트래픽이 늘수록
   * 불리해지는 구조라 조회 경로에서 락을 뺐다.
   *
   * <p>2026-09-28 선언도 {@code ConcurrentHashMap} 으로 바꿨다. {@code Collections.synchronizedMap} 은 맵 자신을
   * 락으로 쓰므로 되돌리면 이 시험이 3 초 뒤 실패한다.
   */
  @Test
  void 기억한_뒤의_조회는_전역_락을_기다리지_않는다() throws Exception {
    load();
    var type =
        org.springframework.core.ResolvableType.forClassWithGenerics(
            io.github.luversof.boot.core.BlueskyModuleProperties.class, CoreProperties.class);
    var names = BlueskyContextHolder.orderedBeanNames(context, type);
    assertThat(names).isNotNull().isNotEmpty();

    var locked = new java.util.concurrent.CountDownLatch(1);
    var release = new java.util.concurrent.CountDownLatch(1);
    var holder =
        new Thread(
            () -> {
              synchronized (BlueskyContextHolder.ORDERED_BEAN_NAMES) {
                locked.countDown();
                try {
                  release.await();
                } catch (InterruptedException e) {
                  Thread.currentThread().interrupt();
                }
              }
            });
    holder.start();
    try {
      locked.await();
      var lookup =
          java.util.concurrent.CompletableFuture.supplyAsync(
              () -> BlueskyContextHolder.orderedBeanNames(context, type));
      assertThat(lookup.get(3, java.util.concurrent.TimeUnit.SECONDS)).isEqualTo(names);
    } finally {
      release.countDown();
      holder.join();
    }
  }

  /**
   * 기억이 버린 context 를 붙잡으면 안 된다 - 닫고 버린 context 는 GC 가 거둬 가야 하고, 거둬진 뒤에는 그 기억도 맵에서 빠져야 한다(안 빠지면 시험처럼
   * context 를 계속 띄우는 곳에서 키가 쌓인다).
   */
  @Test
  void 버린_context_는_거둬지고_기억에서도_빠진다() throws Exception {
    load();
    var type =
        org.springframework.core.ResolvableType.forClassWithGenerics(
            io.github.luversof.boot.core.BlueskyModuleProperties.class, CoreProperties.class);
    var ref = rememberInThrowawayContext(type, context);
    for (int i = 0; i < 50 && ref.get() != null; i++) {
      System.gc();
      Thread.sleep(100);
    }
    assertThat(ref.get()).as("닫고 버린 context 가 수거되지 않았다").isNull();

    // 거둬진 키는 참조 처리 스레드가 큐에 넣은 뒤 다음 조회에서 지워진다 - 큐에 들어가기까지 조금 걸릴 수 있다.
    boolean expunged = false;
    for (int i = 0; i < 50 && !expunged; i++) {
      BlueskyContextHolder.orderedBeanNames(context, type);
      expunged =
          BlueskyContextHolder.ORDERED_BEAN_NAMES.keySet().stream()
              .noneMatch(key -> key.get() == null);
      if (!expunged) {
        System.gc();
        Thread.sleep(100);
      }
    }
    assertThat(expunged).as("거둬진 context 의 키가 맵에 남았다").isTrue();
  }

  /** 키는 동일성으로 같다 - 조회용으로 새로 만든 키로도 기억을 찾고, 다른 context 는 따로다. */
  @Test
  void 키는_같은_context_일_때만_같다() {
    load();
    var other = new AnnotationConfigApplicationContext();
    try {
      var stored = new BlueskyContextHolder.ContextKey(context, null);
      var lookup = new BlueskyContextHolder.ContextKey(context, null);
      assertThat(lookup).isEqualTo(stored).hasSameHashCodeAs(stored);
      assertThat(new BlueskyContextHolder.ContextKey(other, null)).isNotEqualTo(stored);
    } finally {
      other.close();
    }
  }

  private static java.lang.ref.WeakReference<AnnotationConfigApplicationContext>
      rememberInThrowawayContext(
          org.springframework.core.ResolvableType type, AnnotationConfigApplicationContext keep) {
    var other = new AnnotationConfigApplicationContext();
    new ConfigurationWarningsApplicationContextInitializer().initialize(other);
    new BlueskyApplicationContextInitializer().initialize(other);
    other.register(TestConfiguration.class);
    other.refresh();
    assertThat(BlueskyContextHolder.orderedBeanNames(other, type)).isNotNull().isNotEmpty();
    other.close();
    // 초기화기가 ApplicationContextUtil 에 other 를 넣어 두므로 GC 를 보기 전에 되돌린다(이걸 안 하면 이 시험은 늘 실패한다).
    ApplicationContextUtil.setApplicationContext(keep);
    return new java.lang.ref.WeakReference<>(other);
  }

  static final CoreProperties EARLIER_PARENT = new CoreProperties();

  /**
   * parent 를 스스로 갖는 모듈 properties. CoreModuleProperties 를 상속하면 기존 초기화(클래스로 bean 조회)가 둘을 보고 멈추므로
   * 인터페이스를 직접 구현하고 초기화는 건너뛴다.
   */
  static class EarlierModuleProperties
      implements io.github.luversof.boot.core.BlueskyModuleProperties<CoreProperties> {
    private static final long serialVersionUID = 1L;

    @Override
    public CoreProperties getParent() {
      return EARLIER_PARENT;
    }

    @Override
    public void setParent(CoreProperties parent) {
      // 자동 주입을 받지 않는다 - 제 parent 를 지킨다.
    }

    @Override
    public java.util.Map<String, CoreProperties> getModules() {
      return java.util.Map.of();
    }

    @Override
    public void afterPropertiesSet() {
      // 초기화를 건너뛴다.
    }
  }

  @Configuration(proxyBeanMethods = false)
  static class EarlierModuleConfiguration {
    @org.springframework.context.annotation.Bean
    @org.springframework.core.annotation.Order(org.springframework.core.Ordered.HIGHEST_PRECEDENCE)
    EarlierModuleProperties earlierModuleProperties() {
      return new EarlierModuleProperties();
    }
  }

  @Configuration(proxyBeanMethods = false)
  static class PrototypeConfiguration {
    @org.springframework.context.annotation.Bean
    @org.springframework.context.annotation.Scope("prototype")
    StringBuilder prototypeBuilder() {
      return new StringBuilder();
    }
  }

  @Configuration(proxyBeanMethods = false)
  @EnableConfigurationProperties({
    CoreBaseProperties.class,
    CoreProperties.class,
    CoreModuleProperties.class
  })
  static class TestConfiguration {}
}
