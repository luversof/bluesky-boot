package io.github.luversof.boot.core;

import static org.assertj.core.api.Assertions.assertThat;

import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import io.github.luversof.boot.context.BlueskyApplicationContextInitializer;
import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.env.ProfileEnvironmentPostProcessor;
import io.github.luversof.boot.web.DomainGroupProperties;
import io.github.luversof.boot.web.DomainModuleProperties;
import io.github.luversof.boot.web.DomainProperties;

/** 모듈은 그룹 하나에만 속하고, 선언하지 않은 모듈 · 그룹 이름에 준 설정은 경고한다. */
@ExtendWith(OutputCaptureExtension.class)
class ModuleGroupDeclarationTest {

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withInitializer(new BlueskyApplicationContextInitializer())
          .withInitializer(applicationContext ->
              new ProfileEnvironmentPostProcessor()
                  .postProcessEnvironment(applicationContext.getEnvironment(), null))
          .withUserConfiguration(Config.class)
          .withPropertyValues(
              "spring.profiles.active=localdev",
              "bluesky-boot.core.module-names=alpha,beta",
              "bluesky-boot.core.module-groups.g1=alpha,beta",
              "bluesky-boot.web.domain.groups.g1.add-path-patterns=/g1/**");

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
  void moduleGetsItsGroupSettings() {
    contextRunner.run(context -> {
      assertThat(context).hasNotFailed();
      assertThat(context.getBean(DomainModuleProperties.class).getModules().get("beta"))
          .extracting(DomainProperties::getAddPathPatterns)
          .asInstanceOf(InstanceOfAssertFactories.list(String.class))
          .containsExactly("/g1/**");
    });
  }

  @Test
  void moduleInTwoGroupsFailsStartup() {
    contextRunner
        .withPropertyValues("bluesky-boot.core.module-groups.g2=beta")
        .run(context ->
            assertThat(context)
                .getFailure()
                .rootCause()
                .hasMessageContaining("Module 'beta' is declared in more than one group"));
  }

  @Test
  void undeclaredModuleAndGroupSettingsAreWarned(CapturedOutput output) {
    contextRunner
        .withPropertyValues(
            "bluesky-boot.web.domain.modules.typo.add-path-patterns=/typo/**",
            "bluesky-boot.web.domain.groups.nogroup.add-path-patterns=/nogroup/**")
        .run(context -> {
          assertThat(context).hasNotFailed();
          assertThat(output)
              .contains("module 'typo' is not declared")
              .contains("group 'nogroup' is not declared");
        });
  }

  @Configuration(proxyBeanMethods = false)
  @Import(CoreConfiguration.class)
  @EnableConfigurationProperties({
    DomainProperties.class,
    DomainGroupProperties.class,
    DomainModuleProperties.class
  })
  static class Config {}
}
