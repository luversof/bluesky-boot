package io.github.luversof.boot.context.i18n;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = ExternalLocaleProperties.PREFIX)
public class ExternalLocaleGroupProperties extends LocaleGroupProperties {

  private static final long serialVersionUID = 1L;

  /** parent 는 external 빈 하나로만 주입한다 - 상위 클래스의 기본 빈 주입과 따로 두면 실행마다 이기는 쪽이 달랐다. */
  @Override
  @Autowired
  public void setParent(
      @Qualifier(LocaleProperties.EXTERNAL_LOCALE_BEAN_NAME) LocaleProperties parent) {
    super.setParent(parent);
  }
}
