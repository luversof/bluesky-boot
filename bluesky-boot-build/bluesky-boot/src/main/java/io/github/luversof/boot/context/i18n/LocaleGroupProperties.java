package io.github.luversof.boot.context.i18n;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.core.AbstractBlueskyGroupProperties;

@ConfigurationProperties(prefix = LocaleProperties.PREFIX)
public class LocaleGroupProperties extends AbstractBlueskyGroupProperties<LocaleProperties> {

  private static final long serialVersionUID = 1L;

  @Override
  @Autowired
  @Qualifier(LocaleProperties.DEFAULT_BEAN_NAME)
  public void setParent(LocaleProperties parent) {
    super.setParent(parent);
  }
}
