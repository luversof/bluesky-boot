package io.github.luversof.boot.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.core.AbstractBlueskyGroupProperties;

@ConfigurationProperties(prefix = CookieProperties.PREFIX)
public class CookieGroupProperties extends AbstractBlueskyGroupProperties<CookieProperties> {

  private static final long serialVersionUID = 1L;

  @Override
  @Autowired
  @Qualifier(CookieProperties.DEFAULT_BEAN_NAME)
  public void setParent(CookieProperties parent) {
    super.setParent(parent);
  }
}
