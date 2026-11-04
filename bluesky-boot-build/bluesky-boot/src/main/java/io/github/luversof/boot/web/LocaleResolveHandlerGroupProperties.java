package io.github.luversof.boot.web;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.core.AbstractBlueskyGroupProperties;

@ConfigurationProperties(prefix = LocaleResolveHandlerProperties.PREFIX)
public class LocaleResolveHandlerGroupProperties
    extends AbstractBlueskyGroupProperties<LocaleResolveHandlerProperties> {

  private static final long serialVersionUID = 1L;

  public LocaleResolveHandlerGroupProperties(LocaleResolveHandlerProperties parent) {
    setParent(parent);
  }
}
