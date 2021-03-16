package io.github.luversof.context.support;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

public class BlueskyReloadableResourceBundleMessageSource extends ReloadableResourceBundleMessageSource {
	
	
	private Map<String, String> messageSourceMap = Collections.emptyMap();

	@Override
	protected MessageFormat resolveCode(String code, Locale locale) {
		if (messageSourceMap.containsKey(code)) {
			return createMessageFormat(messageSourceMap.get(code), locale);
		}
		return super.resolveCode(code, locale);
	}

	@Override
	protected String resolveCodeWithoutArguments(String code, Locale locale) {
		if (messageSourceMap.containsKey(code)) {
			return messageSourceMap.get(code);
		}
		
		return super.resolveCodeWithoutArguments(code, locale);
	}
	
	public Properties getProperties() {
		return getMergedProperties(LocaleContextHolder.getLocale()).getProperties();
	}
}