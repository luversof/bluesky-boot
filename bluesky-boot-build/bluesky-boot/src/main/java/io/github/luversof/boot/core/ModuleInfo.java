package io.github.luversof.boot.core;

import io.github.luversof.boot.context.i18n.LocaleProperties;
import io.github.luversof.boot.context.i18n.LocaleProperties.LocalePropertiesBuilder;
import io.github.luversof.boot.core.CoreBaseProperties.CoreBasePropertiesBuilder;
import io.github.luversof.boot.core.CoreProperties.CorePropertiesBuilder;
import io.github.luversof.boot.web.CookieProperties;
import io.github.luversof.boot.web.CookieProperties.CookiePropertiesBuilder;

/**
 * enum to provide builders with a convenient way to provide coreProperties settings
 * @author bluesky
 *
 */
public interface ModuleInfo {

	default CoreBasePropertiesBuilder getCoreBasePropertiesBuilder() {
		return CoreBaseProperties.builder();
	}
	
	default CorePropertiesBuilder getCorePropertiesBuilder() {
		return CoreProperties.builder();
	}
	
	default LocalePropertiesBuilder getLocalePropertiesBuilder() {
		return LocaleProperties.builder();
	}
	
	default CookiePropertiesBuilder getCookiePropertiesBuilder() {
		return CookieProperties.builder();
	}

}
