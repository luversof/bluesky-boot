package io.github.luversof.boot.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.core.AbstractBlueskyModuleProperties;

@ConfigurationProperties(prefix = WebProperties.PREFIX)
public class WebModuleProperties extends AbstractBlueskyModuleProperties<WebProperties> {

  private static final long serialVersionUID = 1L;

  @Override
  @Autowired
  public void setParent(WebProperties parent) {
    super.setParent(parent);
  }
}
