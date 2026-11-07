package io.github.luversof.cloud.context.refresh;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.MapPropertySource;

import io.github.luversof.boot.autoconfigure.core.CoreAutoConfiguration;
import io.github.luversof.boot.autoconfigure.uuid.UuidGeneratorAutoConfiguration;
import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.CoreModuleProperties;
import io.github.luversof.boot.uuid.UuidGeneratorGroupProperties;
import io.github.luversof.boot.uuid.UuidGeneratorModuleProperties;
import io.github.luversof.boot.uuid.UuidGeneratorProperties.UuidVersion;
import io.github.luversof.cloud.autoconfigure.BlueskyRefreshEndpointAutoConfiguration;

/**
 * Environment 값을 바꾸고 refresh 했을 때 properties 에 반영되는지 본다.
 *
 * <p>refresher 는 SpringApplication 이 발행하는 ContextRefreshedWithApplicationEvent 가 있어야 동작하므로
 * ApplicationContextRunner 대신 실제 SpringApplication 으로 띄운다.
 */
class BlueskyPropertiesRefresherTests {

  private static final String CORE_GROUP_KEY = "bluesky-boot.core.groups.g1.properties.key";

  private static final String UUID_GROUP_KEY = "bluesky-boot.uuid-generator.groups.g1.version";

  private static final String UUID_MODULE_KEY = "bluesky-boot.uuid-generator.modules.alpha.version";

  private final Map<String, Object> config = new HashMap<>();

  /** 초기값 스냅샷은 JVM 전역 컨텍스트에 computeIfAbsent 로 남으므로 앞선 테스트의 값이 복원되지 않게 비운다. */
  @BeforeEach
  void clearInitialProperties() {
    BlueskyBootContextHolder.getContext().getInitialBlueskyRefreshPropertiesMap().clear();
  }

  /** 대조군: 모듈 값은 refresh 로 바뀐다. */
  @Test
  void moduleValueIsAppliedAfterRefresh() {
    config.put(UUID_MODULE_KEY, "V1");

    try (var context = run()) {
      var modules = context.getBean(UuidGeneratorModuleProperties.class);
      assertThat(modules.getModules().get("alpha").getVersion()).isEqualTo(UuidVersion.V1);

      config.put(UUID_MODULE_KEY, "V6");
      context.getBean(BlueskyPropertiesRefresher.class).refresh();

      assertThat(modules.getModules().get("alpha").getVersion()).isEqualTo(UuidVersion.V6);
    }
  }

  /** 대조군: core 그룹은 refresher 의 우선 갱신 목록에 있다. */
  @Test
  void coreGroupValueIsAppliedAfterRefresh() {
    config.put(CORE_GROUP_KEY, "before");

    try (var context = run()) {
      var modules = context.getBean(CoreModuleProperties.class);
      assertThat(modules.getModules().get("alpha").getProperties())
          .containsEntry("key", "before");

      config.put(CORE_GROUP_KEY, "after");
      context.getBean(BlueskyPropertiesRefresher.class).refresh();

      assertThat(modules.getModules().get("alpha").getProperties()).containsEntry("key", "after");
    }
  }

  @Test
  void nonCoreGroupValueIsAppliedAfterRefresh() {
    config.put(UUID_GROUP_KEY, "V1");

    try (var context = run()) {
      var groups = context.getBean(UuidGeneratorGroupProperties.class);
      var modules = context.getBean(UuidGeneratorModuleProperties.class);
      assertThat(groups.getGroups().get("g1").getVersion()).isEqualTo(UuidVersion.V1);
      assertThat(modules.getModules().get("alpha").getVersion()).isEqualTo(UuidVersion.V1);

      config.put(UUID_GROUP_KEY, "V6");
      context.getBean(BlueskyPropertiesRefresher.class).refresh();

      assertSoftly(softly -> {
        softly.assertThat(groups.getGroups().get("g1").getVersion()).isEqualTo(UuidVersion.V6);
        softly.assertThat(modules.getModules().get("alpha").getVersion()).isEqualTo(UuidVersion.V6);
      });
    }
  }

  private ConfigurableApplicationContext run() {
    return new SpringApplicationBuilder(TestConfiguration.class)
        .web(WebApplicationType.NONE)
        .profiles("localdev")
        .properties(
            "bluesky-boot.core.module-names=alpha,beta",
            "bluesky-boot.core.module-groups.g1=alpha,beta")
        .initializers(applicationContext ->
            applicationContext
                .getEnvironment()
                .getPropertySources()
                .addFirst(new MapPropertySource("mutableTestConfig", config)))
        .run();
  }

  @Configuration(proxyBeanMethods = false)
  @ImportAutoConfiguration({
    RefreshAutoConfiguration.class,
    BlueskyRefreshEndpointAutoConfiguration.class,
    CoreAutoConfiguration.class,
    UuidGeneratorAutoConfiguration.class
  })
  static class TestConfiguration {}
}
