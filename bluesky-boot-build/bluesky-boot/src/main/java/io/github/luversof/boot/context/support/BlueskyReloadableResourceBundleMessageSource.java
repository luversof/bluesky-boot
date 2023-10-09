package io.github.luversof.boot.context.support;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Properties;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * 외부 messageSource 연동을 위해 확장하였으나 구현을 하지 않은 상태임
 * @author bluesky
 *
 */
public class BlueskyReloadableResourceBundleMessageSource extends ReloadableResourceBundleMessageSource {
	
	@Override
	protected MessageFormat resolveCode(String code, Locale locale) {
		return super.resolveCode(code, locale);
	}

	@Override
	protected String resolveCodeWithoutArguments(String code, Locale locale) {
		return super.resolveCodeWithoutArguments(code, locale);
	}
	
	public Properties getProperties() {
		return getMergedProperties(LocaleContextHolder.getLocale()).getProperties();
	}
}