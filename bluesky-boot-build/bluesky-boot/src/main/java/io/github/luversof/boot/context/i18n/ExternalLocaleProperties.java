package io.github.luversof.boot.context.i18n;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.core.ModuleInfo;

@ConfigurationProperties(prefix = ExternalLocaleProperties.PREFIX)
public class ExternalLocaleProperties extends LocaleProperties {

  private static final long serialVersionUID = 1L;

  public static final String PREFIX = "bluesky-boot.external-locale";

  @Override
  protected LocaleProperties presetOf(ModuleInfo moduleInfo) {
    return moduleInfo.getExternalLocaleProperties();
  }
}
