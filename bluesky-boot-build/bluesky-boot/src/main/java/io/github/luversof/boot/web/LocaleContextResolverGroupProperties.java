package io.github.luversof.boot.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.core.AbstractBlueskyGroupProperties;

@ConfigurationProperties(prefix = LocaleContextResolverProperties.PREFIX)
public class LocaleContextResolverGroupProperties
    extends AbstractBlueskyGroupProperties<LocaleContextResolverProperties> {

  private static final long serialVersionUID = 1L;

  @Override
  @Autowired
  public void setParent(LocaleContextResolverProperties parent) {
    super.setParent(parent);
  }
}
