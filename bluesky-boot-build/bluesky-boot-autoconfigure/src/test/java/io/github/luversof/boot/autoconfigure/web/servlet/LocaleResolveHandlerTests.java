package io.github.luversof.boot.autoconfigure.web.servlet;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;

import io.github.luversof.boot.autoconfigure.context.i18n.LocaleAutoConfiguration;
import io.github.luversof.boot.autoconfigure.core.CoreAutoConfiguration;
import io.github.luversof.boot.autoconfigure.web.WebAutoConfiguration;
import io.github.luversof.boot.context.BlueskyApplicationContextInitializer;
import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.web.servlet.i18n.LocaleResolveInfoContainer;
import io.github.luversof.boot.web.servlet.i18n.handler.AcceptHeaderLocaleResolveHandler;
import io.github.luversof.boot.web.servlet.i18n.handler.CookieLocaleResolveHandler;
import jakarta.servlet.http.Cookie;

/** 자동 구성된 locale handler 가 자기 properties 를 찾아 locale 을 고르는지 본다. */
class LocaleResolveHandlerTests {

  private final WebApplicationContextRunner contextRunner =
      new WebApplicationContextRunner()
          .withInitializer(new BlueskyApplicationContextInitializer())
          .withConfiguration(
              AutoConfigurations.of(
                  CoreAutoConfiguration.class,
                  LocaleAutoConfiguration.class,
                  WebAutoConfiguration.class,
                  WebMvcAutoConfiguration.class))
          .withPropertyValues(
              "bluesky-boot.locale.enabled-locales=ko_KR,en_US",
              "bluesky-boot.web.cookie.name=LOCALE");

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
  void cookieHandlerResolvesLocaleFromCookie() {
    contextRunner
        .withPropertyValues(
            "bluesky-boot.web.locale.resolve-handler.cookie.locale-resolve-info-condition=cookie")
        .run(context -> {
          var request = new MockHttpServletRequest();
          request.setCookies(new Cookie("LOCALE", "en-US"));
          var container = new LocaleResolveInfoContainer();

          context
              .getBean(
                  CookieLocaleResolveHandler.DEFAULT_BEAN_NAME, CookieLocaleResolveHandler.class)
              .resolveLocale(request, container);

          assertThat(container.getResolveList().getFirst().getResolveLocale()).isEqualTo(Locale.US);
        });
  }

  /** locale-resolve-info-condition 을 주지 않아도 정확히 맞는 locale 은 고른다. */
  @Test
  void acceptHeaderHandlerWorksWithoutCondition() {
    contextRunner.run(context -> {
      var request = new MockHttpServletRequest();
      request.addHeader(HttpHeaders.ACCEPT_LANGUAGE, "en-US");
      request.addPreferredLocale(Locale.US);
      var container = new LocaleResolveInfoContainer();

      context.getBean(AcceptHeaderLocaleResolveHandler.class).resolveLocale(request, container);

      assertThat(container.getResolveList().getFirst().getResolveLocale()).isEqualTo(Locale.US);
    });
  }
}
