package io.github.luversof.boot.core;

import io.github.luversof.boot.context.i18n.LocaleProperties;
import io.github.luversof.boot.web.CookieProperties;
import io.github.luversof.boot.web.DomainProperties;
import io.github.luversof.boot.web.WebProperties;

/**
 * enum to provide builders with a convenient way to provide coreProperties settings
 * @author bluesky
 *
 */
public interface ModuleInfo {

	default CoreBaseProperties.CoreBasePropertiesBuilder getCoreBasePropertiesBuilder() {
		return CoreBaseProperties.builder();
	}
	
	default CoreProperties.CorePropertiesBuilder getCorePropertiesBuilder() {
		return CoreProperties.builder();
	}
	
	default LocaleProperties.LocalePropertiesBuilder getLocalePropertiesBuilder() {
		return LocaleProperties.builder();
	}
	
	default CookieProperties.CookiePropertiesBuilder getCookiePropertiesBuilder() {
		return CookieProperties.builder();
	}
	
	default DomainProperties.DomainPropertiesBuilder getDomainPropertiesBuilder() {
		return DomainProperties.builder();
	}

	default WebProperties.WebPropertiesBuilder getWebPropertiesBuilder() {
		return WebProperties.builder();
	}
}
