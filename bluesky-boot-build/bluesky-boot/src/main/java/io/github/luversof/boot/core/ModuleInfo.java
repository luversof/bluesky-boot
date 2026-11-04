package io.github.luversof.boot.core;

import java.io.Serializable;

import io.github.luversof.boot.context.i18n.LocaleProperties;
import io.github.luversof.boot.uuid.UuidGeneratorProperties;
import io.github.luversof.boot.web.CookieProperties;
import io.github.luversof.boot.web.DomainProperties;
import io.github.luversof.boot.web.LocaleContextResolverProperties;
import io.github.luversof.boot.web.LocaleResolveHandlerProperties;
import io.github.luversof.boot.web.WebProperties;

/**
 * 모듈(또는 그룹, parent)에 미리 정해 둔 설정 묶음. 주로 enum 으로 구현한다.
 *
 * <p>각 메서드는 그 properties 의 프리셋을 돌려준다 - 정할 값만 채우고 나머지는 null 로 둔 새 객체, 정할 게 없으면 null. 프리셋은 parent 와
 * 그룹 값 위에, 모듈(그룹) 설정 아래에 겹쳐진다.
 *
 * @author bluesky
 */
public interface ModuleInfo extends Serializable {

  default CoreProperties getCoreProperties() {
    return null;
  }

  default LocaleProperties getLocaleProperties() {
    return null;
  }

  default LocaleProperties getExternalLocaleProperties() {
    return getLocaleProperties();
  }

  default CookieProperties getCookieProperties() {
    return null;
  }

  default CookieProperties getExternalCookieProperties() {
    return getCookieProperties();
  }

  default DomainProperties getDomainProperties() {
    return null;
  }

  default WebProperties getWebProperties() {
    return null;
  }

  default LocaleResolveHandlerProperties getLocaleResolveHandlerProperties() {
    return null;
  }

  default LocaleContextResolverProperties getLocaleContextResolverProperties() {
    return null;
  }

  default UuidGeneratorProperties getUuidGeneratorProperties() {
    return null;
  }
}
