package io.github.luversof.boot.core;

import java.io.Serializable;

import io.github.luversof.boot.context.i18n.LocaleProperties;
import io.github.luversof.boot.web.CookieProperties;
import io.github.luversof.boot.web.DomainProperties;
import io.github.luversof.boot.web.LocaleContextResolverProperties;
import io.github.luversof.boot.web.LocaleContextResolverProperties.LocaleContextResolverPropertiesBuilder;
import io.github.luversof.boot.web.LocaleResolveHandlerProperties;
import io.github.luversof.boot.web.LocaleResolveHandlerProperties.LocaleResolveHandlerPropertiesBuilder;
import io.github.luversof.boot.web.WebProperties;

/**
 * enum to provide builders with a convenient way to provide coreProperties settings
 * @author bluesky
 *
 */
public interface ModuleInfo extends Serializable {

	default CoreBaseProperties.CoreBasePropertiesBuilder getCoreBasePropertiesBuilder() {
		return CoreBaseProperties.builder();
	}
	
	default CoreProperties.CorePropertiesBuilder getCorePropertiesBuilder() {
		return CoreProperties.builder();
	}
	
	default LocaleProperties.LocalePropertiesBuilder getLocalePropertiesBuilder() {
		return LocaleProperties.builder();
	}
	
	default LocaleProperties.LocalePropertiesBuilder getExternalLocalePropertiesBuilder() {
		return getLocalePropertiesBuilder();
	}
	
	default CookieProperties.CookiePropertiesBuilder getCookiePropertiesBuilder() {
		return CookieProperties.builder();
	}
	
	default CookieProperties.CookiePropertiesBuilder getExternalCookiePropertiesBuilder() {
		return getCookiePropertiesBuilder();
	}
	
	default DomainProperties.DomainPropertiesBuilder getDomainPropertiesBuilder() {
		return DomainProperties.builder();
	}

	default WebProperties.WebPropertiesBuilder getWebPropertiesBuilder() {
		return WebProperties.builder();
	}

	default LocaleResolveHandlerPropertiesBuilder getLocaleResolveHandlerPropertiesBuilder() {
		return LocaleResolveHandlerProperties.builder();
	}
	
	default LocaleContextResolverPropertiesBuilder getLocaleContextResolverPropertiesBuilder() {
		return LocaleContextResolverProperties.builder();
	}
	
}
