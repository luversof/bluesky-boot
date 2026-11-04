package io.github.luversof.boot.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.core.AbstractBlueskyGroupProperties;

@ConfigurationProperties(prefix = DomainProperties.PREFIX)
public class DomainGroupProperties extends AbstractBlueskyGroupProperties<DomainProperties> {

  private static final long serialVersionUID = 1L;

  @Override
  @Autowired
  public void setParent(DomainProperties parent) {
    super.setParent(parent);
  }
}
