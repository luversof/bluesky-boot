package io.github.luversof.boot.context;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import io.github.luversof.boot.core.BlueskyGroupProperties;
import io.github.luversof.boot.core.BlueskyModuleProperties;
import io.github.luversof.boot.core.BlueskyProperties;
import io.github.luversof.boot.core.CoreProperties;
import io.github.luversof.boot.exception.BlueskyException;

/**
 * Holder of BlueskyContext A holder created by referencing Spring Security's SecurityContextHolder
 */
public final class BlueskyContextHolder {

  private BlueskyContextHolder() {}

  public static final String MODE_THREADLOCAL = "MODE_THREADLOCAL";

  public static final String MODE_INHERITABLETHREADLOCAL = "MODE_INHERITABLETHREADLOCAL";

  public static final String MODE_GLOBAL = "MODE_GLOBAL";

  private static final String MODE_PRE_INITIALIZED = "MODE_PRE_INITIALIZED";

  public static final String SYSTEM_PROPERTY = "bluesky.context.strategy";

  private static String strategyName = System.getProperty(SYSTEM_PROPERTY);

  private static BlueskyContextHolderStrategy strategy;

  private static int initializeCount = 0;

  static {
    initialize();
  }

  private static void initialize() {
    initializeStrategy();
    initializeCount++;
  }

  private static void initializeStrategy() {
    if (MODE_PRE_INITIALIZED.equals(strategyName)) {
      Assert.state(
          strategy != null,
          "When using "
              + MODE_PRE_INITIALIZED
              + ", setContextHolderStrategy must be called with the fully constructed strategy");
      return;
    }
    if (!StringUtils.hasText(strategyName)) {
      // Set default
      strategyName = MODE_THREADLOCAL;
    }

    if (strategyName.equals(MODE_THREADLOCAL)) {
      strategy = new ThreadLocalBlueskyContextHolderStrategy();
      return;
    }
    if (strategyName.equals(MODE_INHERITABLETHREADLOCAL)) {
      strategy = new InheritableThreadLocalBlueskyContextHolderStrategy();
      return;
    }
    if (strategyName.equals(MODE_GLOBAL)) {
      strategy = new GlobalBlueskyContextHolderStrategy();
      return;
    }
    // Try to load a custom strategy
    try {
      var clazz = Class.forName(strategyName);
      var customStrategy = clazz.getConstructor();
      strategy = (BlueskyContextHolderStrategy) customStrategy.newInstance();
    } catch (Exception ex) {
      ReflectionUtils.handleReflectionException(ex);
    }
  }

  /** clear context */
  public static void clearContext() {
    strategy.clearContext();
  }

  /**
   * get context
   *
   * @return BlueskyContext
   */
  public static BlueskyContext getContext() {
    return strategy.getContext();
  }

  /**
   * get initializeCount
   *
   * @return initializeCount
   */
  public static int getInitializeCount() {
    return initializeCount;
  }

  /**
   * set context
   *
   * @param context BlueskyContext
   */
  public static void setContext(BlueskyContext context) {
    strategy.setContext(context);
  }

  /**
   * set context
   *
   * @param moduleName moduleName to set in context
   */
  public static void setContext(String moduleName) {
    setContext(() -> moduleName);
  }

  /** Delegates the creation of a new, empty context to the configured strategy. */
  public static BlueskyContext createEmptyContext() {
    return strategy.createEmptyContext();
  }

  @Override
  public String toString() {
    return "BlueskyContextHolder[strategy='"
        + strategy.getClass().getSimpleName()
        + "'; initializeCount="
        + initializeCount
        + "]";
  }

  /**
   * 현재 module에 대한 Properties 반환
   *
   * @param <T> T extends BlueskyProperties
   * @param blueskyPropertiesClass blueskyProperties extension class
   * @return BlueskyProperties extension object
   */
  public static <T extends BlueskyProperties> T getProperties(Class<T> blueskyPropertiesClass) {
    return getProperties(blueskyPropertiesClass, null);
  }

  /**
   * 같은 class로 여러 properties object를 사용하는 경우 지정된 bean name의 properties object를 호출
   *
   * @param <T> T extends BlueskyProperties
   * @param blueskyPropertiesClass blueskyProperties extension class
   * @param blueskyPropertiesBeanName blueskyProperties bean name
   * @return BlueskyProperties extension object
   */
  public static <T extends BlueskyProperties> T getProperties(
      Class<T> blueskyPropertiesClass, String blueskyPropertiesBeanName) {
    var blueskyModuleProperties =
        getModuleProperties(blueskyPropertiesClass, blueskyPropertiesBeanName);

    // moduleBeanNamesForType가 없으면 BlueskyProperties를 직접 조회
    if (blueskyModuleProperties == null) {
      var applicationContext = ApplicationContextUtil.getApplicationContext();
      if (blueskyPropertiesBeanName
          != null) { // 지정된 BlueskyPropertiesBeanName이 없으면 첫번째 beanName을 사용
        return applicationContext.getBean(blueskyPropertiesBeanName, blueskyPropertiesClass);
      }

      String[] beanNamesForType = applicationContext.getBeanNamesForType(blueskyPropertiesClass);
      if (beanNamesForType.length == 0) {
        throw new BlueskyException("NOT EXIST TARGET BLUESKY PROPERTIES");
      }
      return applicationContext.getBean(beanNamesForType[0], blueskyPropertiesClass);
    }

    return getProperties(blueskyModuleProperties);
  }

  /**
   * (application context, 조회 타입) &rarr; 그 타입 bean 이름들({@code orderedStream} 순서).
   *
   * <p>{@code getBeanNamesForType(ResolvableType)} 은 모든 bean 정의의 제네릭 타입을 훑고, Spring 은 {@code Class}
   * 조회와 달리 이것을 캐시하지 않는다. 그런데 이 경로는 로케일을 물을 때마다(요청 파라미터를 하나 변환할 때마다) 불린다 &mdash; 실측 2026-09-23:
   * api-stock holdingsSnapshotBatch 의 요청 스레드 표본 48% 가 여기였다(DB 왕복 39%, 계산 6%).
   *
   * <p>bean <b>이름</b>만 기억하고 인스턴스는 매번 {@code getBean} 으로 받는다 &mdash; refresh scope 처럼 인스턴스가 바뀌어도
   * 맞는다. context 가 다 뜬 뒤({@code isRunning})에만 기억하고, context 별로 따로 둔다(시험이 context 를 여럿 띄운다). 이름을
   * 인스턴스와 1:1 로 되짚지 못하면(싱글톤이 아닌 bean) 기억하지 않고 예전 방식으로 조회한다.
   *
   * <p>전역 락이 없는 {@link ConcurrentHashMap} 이다(사용자 요청 2026-09-28: 대량 요청에서 모든 요청 스레드가 락 하나를 지나가는
   * {@code Collections.synchronizedMap(new WeakHashMap<>())} 를 걷어냄). 키는 context 를 약하게 잡아 버려진
   * context 를 붙잡지 않고, 거둬진 키는 {@link #STALE_CONTEXT_KEYS} 로 알림 받아 다음 조회 때 지운다({@code WeakHashMap} 이
   * 하던 일).
   */
  static final Map<ContextKey, Map<String, List<String>>> ORDERED_BEAN_NAMES =
      new ConcurrentHashMap<>();

  private static final ReferenceQueue<ApplicationContext> STALE_CONTEXT_KEYS =
      new ReferenceQueue<>();

  /**
   * context 를 약하게 잡는 키. 같은지는 동일성으로 본다({@code equals} 를 재정의한 context 가 있어도 context 별로 따로 둔다). 조회용 키는
   * 큐 없이 만들어 쓰고 버린다.
   */
  static final class ContextKey extends WeakReference<ApplicationContext> {

    private final int hash;

    ContextKey(ApplicationContext applicationContext, ReferenceQueue<ApplicationContext> queue) {
      super(applicationContext, queue);
      this.hash = System.identityHashCode(applicationContext);
    }

    @Override
    public boolean equals(Object other) {
      if (this == other) {
        return true;
      }
      if (!(other instanceof ContextKey key) || key.hash != hash) {
        return false;
      }
      var applicationContext = get();
      return applicationContext != null && applicationContext == key.get();
    }

    @Override
    public int hashCode() {
      return hash;
    }
  }

  private static Map<String, List<String>> namesOf(ApplicationContext applicationContext) {
    expungeStaleContextKeys();
    var byType = ORDERED_BEAN_NAMES.get(new ContextKey(applicationContext, null));
    if (byType != null) {
      return byType;
    }
    return ORDERED_BEAN_NAMES.computeIfAbsent(
        new ContextKey(applicationContext, STALE_CONTEXT_KEYS), _ -> new ConcurrentHashMap<>());
  }

  /** 거둬진 context 의 기억을 지운다. 지울 게 없으면 큐를 한 번 들여다보고 끝난다. */
  private static void expungeStaleContextKeys() {
    Reference<? extends ApplicationContext> stale;
    while ((stale = STALE_CONTEXT_KEYS.poll()) != null) {
      ORDERED_BEAN_NAMES.remove(stale);
    }
  }

  /** 기억할 수 없으면 null(호출하는 쪽이 예전 방식으로 조회한다). */
  static List<String> orderedBeanNames(ApplicationContext applicationContext, ResolvableType type) {
    boolean settled =
        applicationContext instanceof ConfigurableApplicationContext configurable
            && configurable.isRunning();
    String key = type.toString();
    if (settled) {
      var byType = namesOf(applicationContext);
      var remembered = byType.get(key);
      if (remembered != null) {
        return remembered;
      }
      var computed = computeOrderedBeanNames(applicationContext, type);
      if (computed != null) {
        byType.put(key, computed);
      }
      return computed;
    }
    return computeOrderedBeanNames(applicationContext, type);
  }

  private static List<String> computeOrderedBeanNames(
      ApplicationContext applicationContext, ResolvableType type) {
    String[] names = applicationContext.getBeanNamesForType(type);
    if (names.length == 0) {
      return List.of();
    }
    Map<Object, String> nameByInstance = new IdentityHashMap<>();
    for (String name : names) {
      nameByInstance.put(applicationContext.getBean(name), name);
    }
    ObjectProvider<Object> provider = applicationContext.getBeanProvider(type);
    List<String> ordered = provider.orderedStream().map(nameByInstance::get).toList();
    if (ordered.size() != names.length || ordered.contains(null)) {
      return null;
    }
    return ordered;
  }

  @SuppressWarnings("unchecked")
  private static <T extends BlueskyProperties, U extends BlueskyModuleProperties<T>>
      U getModuleProperties(Class<T> blueskyPropertiesClass, String blueskyPropertiesBeanName) {
    var moduleResolvableType =
        ResolvableType.forClassWithGenerics(BlueskyModuleProperties.class, blueskyPropertiesClass);
    var applicationContext = ApplicationContextUtil.getApplicationContext();

    List<String> names = orderedBeanNames(applicationContext, moduleResolvableType);
    if (names != null) {
      if (names.isEmpty()) {
        return null;
      }
      if (blueskyPropertiesBeanName == null) {
        return (U) applicationContext.getBean(names.get(0));
      }
      var parent = applicationContext.getBean(blueskyPropertiesBeanName, blueskyPropertiesClass);
      for (String name : names) {
        U candidate = (U) applicationContext.getBean(name);
        if (candidate.getParent() == parent) {
          return candidate;
        }
      }
      throw new BlueskyException("NOT EXIST TARGET BLUESKY MODULE PROPERTIES");
    }

    String[] moduleBeanNamesForType = applicationContext.getBeanNamesForType(moduleResolvableType);
    if (moduleBeanNamesForType.length == 0) {
      return null;
    }

    ObjectProvider<U> moduleBeanProvider = applicationContext.getBeanProvider(moduleResolvableType);
    if (blueskyPropertiesBeanName == null) {
      return moduleBeanProvider.orderedStream().toList().get(0);
    }
    var parent = applicationContext.getBean(blueskyPropertiesBeanName, blueskyPropertiesClass);
    return moduleBeanProvider.stream()
        .filter(x -> x.getParent() == parent)
        .findFirst()
        .orElseThrow(() -> new BlueskyException("NOT EXIST TARGET BLUESKY MODULE PROPERTIES"));
  }

  private static <T extends BlueskyProperties, U extends BlueskyModuleProperties<T>>
      T getProperties(U blueskyModuleProperties) {
    var moduleName = getContext().getModuleName();
    if (moduleName == null
        || blueskyModuleProperties.getModules() == null
        || !blueskyModuleProperties.getModules().containsKey(moduleName)) {
      return blueskyModuleProperties.getParent();
    }
    return blueskyModuleProperties.getModules().get(moduleName);
  }

  public static <T extends BlueskyProperties> BlueskyGroupProperties<T> getGroupProperties(
      Class<T> blueskyPropertiesClass, String blueskyPropertiesBeanName) {
    var groupResolvableType =
        ResolvableType.forClassWithGenerics(BlueskyGroupProperties.class, blueskyPropertiesClass);
    var applicationContext = ApplicationContextUtil.getApplicationContext();

    List<String> names = orderedBeanNames(applicationContext, groupResolvableType);
    if (names != null) {
      if (names.isEmpty()) {
        return null;
      }
      if (blueskyPropertiesBeanName == null) {
        @SuppressWarnings("unchecked")
        var first = (BlueskyGroupProperties<T>) applicationContext.getBean(names.get(0));
        return first;
      }
      var parent = applicationContext.getBean(blueskyPropertiesBeanName, blueskyPropertiesClass);
      for (String name : names) {
        @SuppressWarnings("unchecked")
        var candidate = (BlueskyGroupProperties<T>) applicationContext.getBean(name);
        if (candidate.getParent() == parent) {
          return candidate;
        }
      }
      return null;
    }

    String[] groupBeanNamesForType = applicationContext.getBeanNamesForType(groupResolvableType);
    if (groupBeanNamesForType.length == 0) {
      return null;
    }

    ObjectProvider<BlueskyGroupProperties<T>> groupBeanProvider =
        applicationContext.getBeanProvider(groupResolvableType);

    if (blueskyPropertiesBeanName == null) {
      return groupBeanProvider.orderedStream().toList().get(0);
    }
    var parent = applicationContext.getBean(blueskyPropertiesBeanName, blueskyPropertiesClass);
    return groupBeanProvider.stream()
        .filter(x -> x.getParent() == parent)
        .findFirst()
        .orElseGet(() -> null);
  }

  /**
   * coreProperties의 경우 가장 자주 쓰이기 때문에 기본 제공
   *
   * @return CoreProperties
   */
  public static CoreProperties getCoreProperties() {
    return getProperties(CoreProperties.class);
  }
}
