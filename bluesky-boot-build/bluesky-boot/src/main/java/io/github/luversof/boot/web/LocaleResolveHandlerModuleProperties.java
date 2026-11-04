package io.github.luversof.boot.web;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.core.AbstractBlueskyModuleProperties;

@ConfigurationProperties(prefix = LocaleResolveHandlerProperties.PREFIX)
public class LocaleResolveHandlerModuleProperties
    extends AbstractBlueskyModuleProperties<LocaleResolveHandlerProperties> {

  private static final long serialVersionUID = 1L;

  public LocaleResolveHandlerModuleProperties(LocaleResolveHandlerProperties parent) {
    setParent(parent);
  }
}
