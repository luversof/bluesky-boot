package io.github.luversof.boot.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.github.luversof.boot.core.BlueskyPropertiesOverlay;
import io.github.luversof.boot.web.LocaleContextResolverProperties.LocaleContextResolveHandlerPreset;
import io.github.luversof.boot.web.servlet.i18n.LocaleResolveHandler;
import io.github.luversof.boot.web.servlet.i18n.handler.AcceptHeaderLocaleResolveHandler;
import io.github.luversof.boot.web.servlet.i18n.handler.CookieLocaleResolveHandler;

/** handler 는 설정된 이름 순서대로, 없는 이름은 건너뛰고 고른다. */
class LocaleContextResolverPropertiesTest {

  @Test
  void handlersFollowConfiguredOrderAndSkipMissingNames() {
    var cookie = mock(LocaleResolveHandler.class);
    var acceptHeader = mock(LocaleResolveHandler.class);
    var properties = new LocaleContextResolverProperties();
    properties.setLocaleResolveHandlerBeanNames(List.of("acceptHeader", "missing", "cookie"));

    var handlers =
        properties.getLocaleResolveHandlerList(
            Map.of("cookie", cookie, "acceptHeader", acceptHeader));

    assertThat(handlers).containsExactly(acceptHeader, cookie);
  }

  /** preset 만 주면 preset 의 목록, 모듈이 preset 만 바꿔도 먹고, 명시한 목록은 preset 보다 이긴다. */
  @Test
  void presetDecidesWhenNoListIsGiven() {
    var cookie = mock(LocaleResolveHandler.class);
    var acceptHeader = mock(LocaleResolveHandler.class);
    var handlers =
        Map.of(
            CookieLocaleResolveHandler.DEFAULT_BEAN_NAME, cookie,
            AcceptHeaderLocaleResolveHandler.DEFAULT_BEAN_NAME, acceptHeader);
    var parent = new LocaleContextResolverProperties();
    parent.setPreset(LocaleContextResolveHandlerPreset.COOKIE);
    parent.applyDefaults();

    var inherited = merged(parent, new LocaleContextResolverProperties());
    var modulePreset = new LocaleContextResolverProperties();
    modulePreset.setPreset(LocaleContextResolveHandlerPreset.ACCEPT_HEADER);
    var overridden = merged(parent, modulePreset);
    var moduleList = new LocaleContextResolverProperties();
    moduleList.setLocaleResolveHandlerBeanNames(
        List.of(AcceptHeaderLocaleResolveHandler.DEFAULT_BEAN_NAME));
    var explicit = merged(parent, moduleList);

    assertThat(inherited.getLocaleResolveHandlerList(handlers)).containsExactly(cookie);
    assertThat(overridden.getLocaleResolveHandlerList(handlers)).containsExactly(acceptHeader);
    assertThat(explicit.getLocaleResolveHandlerList(handlers)).containsExactly(acceptHeader);
  }

  private static LocaleContextResolverProperties merged(
      LocaleContextResolverProperties parent, LocaleContextResolverProperties module) {
    var merged = new LocaleContextResolverProperties();
    BlueskyPropertiesOverlay.overlay(parent, merged);
    BlueskyPropertiesOverlay.overlay(module, merged);
    merged.applyDefaults();
    return merged;
  }
}
