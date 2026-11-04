package io.github.luversof.boot.web;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.core.ModuleInfo;

@ConfigurationProperties(prefix = ExternalCookieProperties.PREFIX)
public class ExternalCookieProperties extends CookieProperties {

  private static final long serialVersionUID = 1L;

  public static final String PREFIX = "bluesky-boot.web.external-cookie";

  @Override
  protected CookieProperties presetOf(ModuleInfo moduleInfo) {
    return moduleInfo.getExternalCookieProperties();
  }
}
