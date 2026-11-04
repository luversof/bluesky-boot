package io.github.luversof.boot.uuid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.core.AbstractBlueskyModuleProperties;

@ConfigurationProperties(prefix = UuidGeneratorProperties.PREFIX)
public class UuidGeneratorModuleProperties
    extends AbstractBlueskyModuleProperties<UuidGeneratorProperties> {

  private static final long serialVersionUID = 1L;

  @Override
  @Autowired
  public void setParent(UuidGeneratorProperties parent) {
    super.setParent(parent);
  }
}
