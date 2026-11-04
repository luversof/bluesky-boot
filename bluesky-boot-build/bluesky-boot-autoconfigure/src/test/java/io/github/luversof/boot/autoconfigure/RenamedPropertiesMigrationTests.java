package io.github.luversof.boot.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Configuration;

import io.github.luversof.boot.autoconfigure.context.i18n.LocaleAutoConfiguration;
import io.github.luversof.boot.autoconfigure.core.CoreAutoConfiguration;
import io.github.luversof.boot.context.i18n.LocaleProperties;
import io.github.luversof.boot.core.CoreBaseProperties;

/**
 * 이름을 바꾼 옛 키도 properties-migrator 가 있으면 새 키로 옮겨 바인딩된다(경고 로그와 함께).
 *
 * <p>map 안쪽 키({@code modules.*.enable-locale-list} 같은)는 메타데이터로 표현할 수 없어 옮겨지지 않는다 - 설정 파일을 직접 고쳐야
 * 한다.
 */
class RenamedPropertiesMigrationTests {

  @Test
  void oldKeysStillBindThroughTheMigrator() {
    try (var context =
        new SpringApplicationBuilder(Config.class)
            .web(WebApplicationType.NONE)
            .properties(
                "spring.profiles.active=localdev",
                "spring.cloud.config.enabled=false",
                "bluesky-boot.core.module-name-set=alpha,beta",
                "bluesky-boot.core.log-except-exception-list=FooException",
                "bluesky-boot.locale.enable-locale-list=fr_FR")
            .run()) {
      var coreBase = context.getBean(CoreBaseProperties.class);
      assertThat(coreBase.getModuleNames()).containsExactlyInAnyOrder("alpha", "beta");
      assertThat(coreBase.getLogExcludedExceptions()).contains("FooException");
      assertThat(
              context
                  .getBean(LocaleProperties.DEFAULT_BEAN_NAME, LocaleProperties.class)
                  .getEnabledLocales())
          .isEqualTo(List.of(Locale.FRANCE));
    }
  }

  @Configuration(proxyBeanMethods = false)
  @ImportAutoConfiguration({CoreAutoConfiguration.class, LocaleAutoConfiguration.class})
  static class Config {}
}
