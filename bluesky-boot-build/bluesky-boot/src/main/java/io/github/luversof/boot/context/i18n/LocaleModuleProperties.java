package io.github.luversof.boot.context.i18n;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.core.AbstractBlueskyModuleProperties;

@ConfigurationProperties(prefix = LocaleProperties.PREFIX)
public class LocaleModuleProperties extends AbstractBlueskyModuleProperties<LocaleProperties> {

  private static final long serialVersionUID = 1L;

  public static final String DEFAULT_BEAN_NAME =
      "bluesky-boot.locale-io.github.luversof.boot.context.i18n.LocaleModuleProperties";
  public static final String EXTERNAL_LOCALE_BEAN_NAME =
      "bluesky-boot.external-locale-io.github.luversof.boot.context.i18n.ExternalLocaleModuleProperties";

  @Override
  @Autowired
  public void setParent(
      @Qualifier(LocaleProperties.DEFAULT_BEAN_NAME) LocaleProperties localeProperties) {
    super.setParent(localeProperties);
  }
}
