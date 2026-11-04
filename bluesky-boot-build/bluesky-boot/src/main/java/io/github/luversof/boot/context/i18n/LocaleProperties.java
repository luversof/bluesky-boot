package io.github.luversof.boot.context.i18n;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.core.AbstractBlueskyProperties;
import io.github.luversof.boot.core.ModuleInfo;

/** 기본적인 Locale 관련 설정을 관리 */
@ConfigurationProperties(prefix = LocaleProperties.PREFIX)
public class LocaleProperties extends AbstractBlueskyProperties<LocaleProperties>
    implements BeanNameAware {

  private static final long serialVersionUID = 1L;

  public static final String PREFIX = "bluesky-boot.locale";

  public static final String DEFAULT_BEAN_NAME =
      "bluesky-boot.locale-io.github.luversof.boot.context.i18n.LocaleProperties";
  public static final String EXTERNAL_LOCALE_BEAN_NAME =
      "bluesky-boot.external-locale-io.github.luversof.boot.context.i18n.ExternalLocaleProperties";

  private String beanName;

  /** 사용 가능 로케일 목록, 배열의 첫번째 로케일이 defaultLocale로 처리되며 만약 설정하지 않은 경우 Locale.default()를 사용 */
  private List<Locale> enabledLocales;

  public LocaleProperties() {}

  @Override
  public void setBeanName(String beanName) {
    this.beanName = beanName;
  }

  @Override
  public String getBeanName() {
    return beanName;
  }

  public List<Locale> getEnabledLocales() {
    return enabledLocales;
  }

  public void setEnabledLocales(List<Locale> enabledLocales) {
    this.enabledLocales = enabledLocales;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass() || !super.equals(o)) {
      return false;
    }
    LocaleProperties that = (LocaleProperties) o;
    return (beanName != null ? beanName.equals(that.beanName) : that.beanName == null)
        && (enabledLocales != null
            ? enabledLocales.equals(that.enabledLocales)
            : that.enabledLocales == null);
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (beanName != null ? beanName.hashCode() : 0);
    result = 31 * result + (enabledLocales != null ? enabledLocales.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "LocaleProperties{"
        + "beanName='"
        + beanName
        + '\''
        + ", enabledLocales="
        + enabledLocales
        + '}';
  }

  public Locale getDefaultLocale() {
    if (enabledLocales == null || enabledLocales.isEmpty()) {
      return Locale.getDefault();
    }
    return enabledLocales.get(0);
  }

  @Override
  protected LocaleProperties presetOf(ModuleInfo moduleInfo) {
    return moduleInfo.getLocaleProperties();
  }

  @Override
  protected void applyDefaults() {
    if (enabledLocales == null) {
      enabledLocales = new ArrayList<>();
    }
  }
}
