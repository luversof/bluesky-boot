package io.github.luversof.boot.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

import io.github.luversof.boot.autoconfigure.context.i18n.LocaleAutoConfiguration;
import io.github.luversof.boot.autoconfigure.core.CoreAutoConfiguration;
import io.github.luversof.boot.autoconfigure.uuid.UuidGeneratorAutoConfiguration;
import io.github.luversof.boot.autoconfigure.web.WebAutoConfiguration;
import io.github.luversof.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import io.github.luversof.boot.context.BlueskyApplicationContextInitializer;
import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.BlueskyGroupProperties;
import io.github.luversof.boot.core.BlueskyModuleProperties;
import io.github.luversof.boot.core.BlueskyProperties;
import io.github.luversof.boot.env.ProfileEnvironmentPostProcessor;
import tools.jackson.databind.MapperFeature;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.json.JsonMapper;

/**
 * parent &rarr; group &rarr; ModuleInfo 프리셋 &rarr; module 순으로 합친 결과를 통째로 기록해 둔 특성 시험.
 *
 * <p>프로퍼티 클래스 구조를 바꿀 때(builder 와 합치기 코드 정리) 결과가 그대로인지 본다. 기대값이 바뀌는 게 맞으면 {@code
 * -Dcharacterization.update=true} 로 다시 기록하고 차이를 검토한다.
 */
class BlueskyPropertiesMergeCharacterizationTests {

  private static final Path EXPECTED =
      Path.of("src/test/resources/characterization/bluesky-properties-merge.json");

  private static final String TEST_MODULE_INFO =
      "T(io.github.luversof.boot.autoconfigure.core.constant.TestModuleInfo)";

  private final WebApplicationContextRunner contextRunner =
      new WebApplicationContextRunner()
          .withInitializer(new BlueskyApplicationContextInitializer())
          .withInitializer(applicationContext ->
              new ProfileEnvironmentPostProcessor()
                  .postProcessEnvironment(applicationContext.getEnvironment(), null))
          .withConfiguration(
              AutoConfigurations.of(
                  CoreAutoConfiguration.class,
                  LocaleAutoConfiguration.class,
                  WebAutoConfiguration.class,
                  WebMvcAutoConfiguration.class,
                  UuidGeneratorAutoConfiguration.class))
          .withPropertyValues(
              "spring.profiles.active=localdev",
              // modules · group · ModuleInfo preset
              "bluesky-boot.core.module-names=alpha,beta,gamma",
              "bluesky-boot.core.module-groups.g1=alpha,beta",
              "bluesky-boot.core.modules.alpha.module-info=" + TEST_MODULE_INFO + ".TEST",
              "bluesky-boot.core.groups.g1.module-info=" + TEST_MODULE_INFO + ".TEST2",
              // gamma 는 external-locale 설정이 없다 - TEST2 프리셋(en_CA)이 그대로 보여야 한다
              "bluesky-boot.core.modules.gamma.module-info=" + TEST_MODULE_INFO + ".TEST2",
              "bluesky-boot.core.properties.a=parent-a",
              "bluesky-boot.core.modules.beta.properties.b=beta-b",
              // locale
              "bluesky-boot.locale.enabled-locales=ko_KR",
              "bluesky-boot.locale.modules.gamma.enabled-locales=en_US,ja_JP",
              // alpha 의 ModuleInfo(TEST) 프리셋은 ko_KR - 모듈 설정이 이겨야 한다
              "bluesky-boot.external-locale.enabled-locales=ko_KR",
              "bluesky-boot.external-locale.modules.alpha.enabled-locales=fr_FR",
              "bluesky-boot.external-locale.modules.beta.enabled-locales=zh_TW",
              // domain
              "bluesky-boot.web.domain.web=https://parent.bluesky.local/",
              "bluesky-boot.web.domain.modules.alpha.web=https://alpha.bluesky.local/",
              "bluesky-boot.web.domain.groups.g1.mobile-web=https://m.bluesky.local/",
              "bluesky-boot.web.domain.modules.beta.add-path-patterns=/beta/**",
              // cookie
              "bluesky-boot.web.cookie.name=PARENT",
              "bluesky-boot.web.cookie.max-age=60",
              "bluesky-boot.web.cookie.groups.g1.secure=true",
              "bluesky-boot.web.cookie.modules.beta.name=BETA",
              "bluesky-boot.web.external-cookie.name=EXT",
              "bluesky-boot.web.external-cookie.modules.alpha.max-age=10",
              // locale resolve
              "bluesky-boot.web.locale.context-resolver.preset=COOKIE",
              "bluesky-boot.web.locale.context-resolver.modules.beta.locale-resolve-handler-bean-names=acceptHeaderLocaleResolveHandler",
              "bluesky-boot.web.locale.resolve-handler.cookie.locale-resolve-info-condition=cookie",
              "bluesky-boot.web.locale.resolve-handler.cookie.set-representative-condition=overwrite-if-not-exists",
              "bluesky-boot.web.locale.resolve-handler.cookie.modules.gamma.pre-locale-resolve-info-condition=use-first",
              // web · uuid
              "bluesky-boot.web.check-not-supported-browser=false",
              "bluesky-boot.web.modules.gamma.check-not-supported-browser=true",
              "bluesky-boot.uuid-generator.version=V4",
              "bluesky-boot.uuid-generator.modules.alpha.version=V6");

  private final JsonMapper jsonMapper =
      JsonMapper.builder()
          .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)
          .enable(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
          .enable(SerializationFeature.INDENT_OUTPUT)
          .build();

  @BeforeEach
  void clearSharedContext() {
    // BlueskyBootContext 는 JVM 전체에 하나라 앞선 시험의 모듈 이름이 남는다
    var context = BlueskyBootContextHolder.getContext();
    context.getModuleNames().clear();
    context.getModuleGroups().clear();
    context.getModuleInfoMap().clear();
    context.getGroupModuleInfoMap().clear();
    context.getInitialBlueskyRefreshPropertiesMap().clear();
    context.setParentModuleInfo(null);
  }

  @Test
  void mergedPropertiesStayTheSame() {
	  
    contextRunner.run(context -> {
      assertThat(context).hasNotFailed();
      Map<String, Object> merged = new TreeMap<>();
      context.getBeansOfType(BlueskyProperties.class).forEach(merged::put);
      context
          .getBeansOfType(BlueskyGroupProperties.class)
          .forEach((name, bean) -> merged.put(name, bean.getGroups()));
      context
          .getBeansOfType(BlueskyModuleProperties.class)
          .forEach((name, bean) -> merged.put(name, bean.getModules()));
      var actual = jsonMapper.writeValueAsString(merged).replace("\r\n", "\n");

      if (Boolean.getBoolean("characterization.update") || !Files.exists(EXPECTED)) {
        Files.createDirectories(EXPECTED.getParent());
        Files.writeString(EXPECTED, actual, StandardCharsets.UTF_8);
      }
      var expected = Files.readString(EXPECTED, StandardCharsets.UTF_8).replace("\r\n", "\n");
      if (!actual.equals(expected)) {
        Files.writeString(
            Path.of("target/bluesky-properties-merge-actual.json"), actual, StandardCharsets.UTF_8);
      }
      assertThat(actual).isEqualTo(expected);
    });
  }
}
