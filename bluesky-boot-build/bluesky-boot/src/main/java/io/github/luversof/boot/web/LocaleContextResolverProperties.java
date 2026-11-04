package io.github.luversof.boot.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.github.luversof.boot.core.AbstractBlueskyProperties;
import io.github.luversof.boot.core.ModuleInfo;
import io.github.luversof.boot.web.servlet.i18n.LocaleResolveHandler;
import io.github.luversof.boot.web.servlet.i18n.handler.AcceptHeaderLocaleResolveHandler;
import io.github.luversof.boot.web.servlet.i18n.handler.CookieLocaleResolveHandler;

/** LocaleContextResovler에서 사용할 localeContextResolveHandler를 설정 */
@ConfigurationProperties(prefix = LocaleContextResolverProperties.PREFIX)
public class LocaleContextResolverProperties
    extends AbstractBlueskyProperties<LocaleContextResolverProperties> {

  private static final Logger log = LoggerFactory.getLogger(LocaleContextResolverProperties.class);

  private static final long serialVersionUID = 1L;

  public static final String PREFIX = "bluesky-boot.web.locale.context-resolver";

  /** 목록에 대한 preset 제공 */
  private LocaleContextResolveHandlerPreset preset;

  /** 사용할 localeResolveHandler beanName 목록, 순서대로 수행됨 */
  private List<String> localeResolveHandlerBeanNames;

  public LocaleContextResolverProperties() {}

  public LocaleContextResolveHandlerPreset getPreset() {
    return preset;
  }

  public void setPreset(LocaleContextResolveHandlerPreset preset) {
    this.preset = preset;
  }

  public List<String> getLocaleResolveHandlerBeanNames() {
    return localeResolveHandlerBeanNames;
  }

  public void setLocaleResolveHandlerBeanNames(List<String> localeResolveHandlerBeanNames) {
    this.localeResolveHandlerBeanNames = localeResolveHandlerBeanNames;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass() || !super.equals(o)) {
      return false;
    }
    LocaleContextResolverProperties that = (LocaleContextResolverProperties) o;
    return preset == that.preset
        && Objects.equals(localeResolveHandlerBeanNames, that.localeResolveHandlerBeanNames);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), preset, localeResolveHandlerBeanNames);
  }

  @Override
  public String toString() {
    return "LocaleContextResolverProperties{"
        + "preset="
        + preset
        + ", localeResolveHandlerBeanNames="
        + localeResolveHandlerBeanNames
        + '}';
  }

  /**
   * 설정된 이름 순서대로 handler 를 고른다. 없는 이름은 건너뛴다. 목록을 주지 않았으면 preset 의 목록을 쓴다(명시한 목록이 preset 보다 이긴다).
   *
   * @param localeResolveHandlers bean 이름 &rarr; handler (resolver 가 만들어질 때 한 번 받은 것)
   */
  @JsonIgnore
  public List<LocaleResolveHandler> getLocaleResolveHandlerList(
      Map<String, LocaleResolveHandler> localeResolveHandlers) {
    var beanNames =
        localeResolveHandlerBeanNames != null
            ? localeResolveHandlerBeanNames
            : preset != null ? preset.getLocaleResolveHandlerBeanNames() : null;
    if (beanNames == null) {
      return Collections.emptyList();
    }

    List<LocaleResolveHandler> localeResolveHandlerList = new ArrayList<>();
    beanNames.forEach(beanName -> {
      var localeResolveHandler = localeResolveHandlers.get(beanName);
      if (localeResolveHandler == null) {
        log.warn("LocaleResolveHandler bean {} is not exist", beanName);
        return;
      }
      localeResolveHandlerList.add(localeResolveHandler);
    });
    return localeResolveHandlerList;
  }

  public enum LocaleContextResolveHandlerPreset {
    BASIC(
        List.of(
            CookieLocaleResolveHandler.DEFAULT_BEAN_NAME,
            AcceptHeaderLocaleResolveHandler.DEFAULT_BEAN_NAME)),
    COOKIE(List.of(CookieLocaleResolveHandler.DEFAULT_BEAN_NAME)),
    ACCEPT_HEADER(List.of(AcceptHeaderLocaleResolveHandler.DEFAULT_BEAN_NAME));

    private final List<String> localeResolveHandlerBeanNames;

    LocaleContextResolveHandlerPreset(List<String> localeResolveHandlerBeanNames) {
      this.localeResolveHandlerBeanNames = localeResolveHandlerBeanNames;
    }

    public List<String> getLocaleResolveHandlerBeanNames() {
      return localeResolveHandlerBeanNames;
    }
  }

  /**
   * preset이 지정되어 있는 경우 별도 설정이 없으면 preset을 localeResolveHandlerBeanNames에 설정 모듈별 properties는 그럼 어떻게
   * 동작하나?
   */
  @Override
  protected LocaleContextResolverProperties presetOf(ModuleInfo moduleInfo) {
    return moduleInfo.getLocaleContextResolverProperties();
  }

  @Override
  protected void applyDefaults() {
    // preset 만 준 경우 목록은 비워 둔다 - 고를 때 preset 으로 정한다(모듈이 preset 만 바꿔도 먹게)
    if (localeResolveHandlerBeanNames == null && preset == null) {
      localeResolveHandlerBeanNames = new ArrayList<>();
    }
  }
}
