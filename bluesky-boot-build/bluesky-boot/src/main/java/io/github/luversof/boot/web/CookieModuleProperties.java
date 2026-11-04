package io.github.luversof.boot.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.core.AbstractBlueskyModuleProperties;

@ConfigurationProperties(prefix = CookieProperties.PREFIX)
public class CookieModuleProperties extends AbstractBlueskyModuleProperties<CookieProperties> {

  private static final long serialVersionUID = 1L;

  public static final String DEFAULT_BEAN_NAME =
      "bluesky-boot.web.cookie-io.github.luversof.boot.web.CookieModuleProperties";
  public static final String EXTERNAL_COOKIE_BEAN_NAME =
      "bluesky-boot.web.external-cookie-io.github.luversof.boot.web.ExternalCookieModuleProperties";

  @Override
  @Autowired
  @Qualifier(CookieProperties.DEFAULT_BEAN_NAME)
  public void setParent(CookieProperties parent) {
    super.setParent(parent);
  }
}
