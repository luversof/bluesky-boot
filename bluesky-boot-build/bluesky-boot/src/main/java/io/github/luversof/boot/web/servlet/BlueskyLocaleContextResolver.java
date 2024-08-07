package io.github.luversof.boot.web.servlet;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.web.servlet.LocaleContextResolver;

import io.github.luversof.boot.context.BlueskyContextHolder;
import io.github.luversof.boot.web.servlet.i18n.LocaleContextResolveInfoContainer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BlueskyLocaleContextResolver implements LocaleContextResolver {
	
	public static final String LOCALE_CONTEXT_REQUEST_ATTRIBUTE_NAME = BlueskyLocaleContextResolver.class.getName() + ".LOCALE_CONTEXT";

	@Override
	public LocaleContext resolveLocaleContext(HttpServletRequest request) {
		var localeContext = (LocaleContext) request.getAttribute(LOCALE_CONTEXT_REQUEST_ATTRIBUTE_NAME);
		if (localeContext != null) {
			return localeContext;
		}
		
		var localeContextResolveInfoContainer = new LocaleContextResolveInfoContainer();
		
		BlueskyContextHolder.getProperties(LocaleContextResolverProperties.class).getLocaleResolveHandlerList().forEach(x -> x.resolveLocaleContext(request, localeContextResolveInfoContainer));
		
		localeContext = localeContextResolveInfoContainer.getLocaleContext();
		request.setAttribute(LOCALE_CONTEXT_REQUEST_ATTRIBUTE_NAME, localeContext);
		return localeContext;
	}
	

	@Override
	public void setLocaleContext(HttpServletRequest request, HttpServletResponse response, LocaleContext localeContext) {
		var localeResolveInfoContainer = new LocaleContextResolveInfoContainer();
		BlueskyContextHolder.getProperties(LocaleContextResolverProperties.class).getLocaleResolveHandlerList().forEach(x -> x.setLocaleContext(request, response, localeContext, localeResolveInfoContainer));
		
		request.setAttribute(LOCALE_CONTEXT_REQUEST_ATTRIBUTE_NAME, localeResolveInfoContainer.getLocaleContext());
	}

}
