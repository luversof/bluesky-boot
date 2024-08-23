package io.github.luversof.boot.web.servlet;

import java.util.Locale;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.web.servlet.LocaleContextResolver;

import io.github.luversof.boot.context.BlueskyContextHolder;
import io.github.luversof.boot.web.LocaleContextResolverProperties;
import io.github.luversof.boot.web.servlet.i18n.LocaleResolveInfoContainer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BlueskyLocaleContextResolver implements LocaleContextResolver {
	
	public static final String LOCALE_REQUEST_ATTRIBUTE_NAME = LocaleContextResolver.class.getName() + ".LOCALE";

	@Override
	public LocaleContext resolveLocaleContext(HttpServletRequest request) {
		return () -> {
			if (request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME) != null) {
				return (Locale) request.getAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME);
			}
			
			var localeResolveInfoContainer = new LocaleResolveInfoContainer();
			BlueskyContextHolder.getProperties(LocaleContextResolverProperties.class).getLocaleResolveHandlerList().forEach(x -> x.resolveLocale(request, localeResolveInfoContainer));
			
			var locale = localeResolveInfoContainer.getLocale();
			request.setAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME, locale);
			
			return locale;
		};
	}

	@Override
	public void setLocaleContext(HttpServletRequest request, HttpServletResponse response, LocaleContext localeContext) {
		var localeResolveInfoContainer = new LocaleResolveInfoContainer();
		BlueskyContextHolder.getProperties(LocaleContextResolverProperties.class).getLocaleResolveHandlerList().forEach(x -> x.setLocale(request, response, localeContext.getLocale(), localeResolveInfoContainer));
		
		request.setAttribute(LOCALE_REQUEST_ATTRIBUTE_NAME, localeResolveInfoContainer.getLocale());
	}

}
