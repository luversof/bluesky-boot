package io.github.luversof.boot.autoconfigure;

import static io.github.luversof.boot.autoconfigure.AutoConfigurationTestInfo.BASE_PROPERTY;
import static io.github.luversof.boot.autoconfigure.AutoConfigurationTestInfo.CORE_MESSAGESOURCE_CONFIGURATION;
import static io.github.luversof.boot.autoconfigure.AutoConfigurationTestInfo.CORE_MESSAGESOURCE_USER_CONFIGURATION;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

import io.github.luversof.boot.autoconfigure.core.servlet.CoreMvcAutoConfiguration;
import io.github.luversof.boot.autoconfigure.security.crypto.encrypt.TextEncryptAutoConfiguration;
import io.github.luversof.boot.context.support.MessageSourceDevCheckController;
import io.github.luversof.boot.core.CoreDevCheckController;
import io.github.luversof.boot.security.crypto.encrypt.TextEncryptDevCheckController;
import io.github.luversof.boot.test.context.runner.BlueskyApplicationContextRunner;

/**
 * dev-check 컨트롤러는 {@code bluesky-boot.dev-check.enabled=true} 일 때만 뜬다.
 *
 * <p>이 컨트롤러들은 {@code @RestController} 라서, devcheck 라이브러리를 꺼도(경로 prefix 가 빠질 뿐) 원래 경로로 그대로 노출됐다.
 * CoreDevCheckController 는 모든 properties 빈을 내보낸다.
 */
class DevCheckControllerConditionTests {

  private static final String ENABLED = "bluesky-boot.dev-check.enabled=true";

  private final WebApplicationContextRunner coreMvc =
      new WebApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(CoreMvcAutoConfiguration.class));

  private final ApplicationContextRunner textEncrypt =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(TextEncryptAutoConfiguration.class));

  private final BlueskyApplicationContextRunner messageSource =
      BlueskyApplicationContextRunner.get()
          .withPropertyValues(BASE_PROPERTY)
          .withConfiguration(AutoConfigurations.of(CORE_MESSAGESOURCE_CONFIGURATION))
          .withUserConfiguration(CORE_MESSAGESOURCE_USER_CONFIGURATION);

  @Test
  void offByDefault() {
    coreMvc.run(context -> assertThat(context).doesNotHaveBean(CoreDevCheckController.class));
    textEncrypt.run(
        context -> assertThat(context).doesNotHaveBean(TextEncryptDevCheckController.class));
    messageSource.run(
        context -> assertThat(context).doesNotHaveBean(MessageSourceDevCheckController.class));
  }

  @Test
  void onWhenEnabled() {
    coreMvc
        .withPropertyValues(ENABLED)
        .run(context -> assertThat(context).hasSingleBean(CoreDevCheckController.class));
    textEncrypt
        .withPropertyValues(ENABLED)
        .run(context -> assertThat(context).hasSingleBean(TextEncryptDevCheckController.class));
    messageSource
        .withPropertyValues(ENABLED)
        .run(context -> assertThat(context).hasSingleBean(MessageSourceDevCheckController.class));
  }
}
