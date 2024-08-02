package io.github.luversof.boot.web.servlet.i18n;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.web.servlet.LocaleContextResolver;

import io.github.luversof.boot.context.BlueskyContextHolder;
import io.github.luversof.boot.web.servlet.LocaleContextResolverProperties;
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
		
		var localeResolveInfoContainer = new LocaleResolveInfoContainer();
		
		BlueskyContextHolder.getProperties(LocaleContextResolverProperties.class).getLocaleResolveHandlerList().forEach(x -> x.resolveLocaleContext(request, localeResolveInfoContainer));
		
		localeContext = localeResolveInfoContainer.getLocaleContext();
		request.setAttribute(LOCALE_CONTEXT_REQUEST_ATTRIBUTE_NAME, localeContext);
		return localeContext;
	}
	

	@Override
	public void setLocaleContext(HttpServletRequest request, HttpServletResponse response, LocaleContext localeContext) {
		var localeResolveInfoContainer = new LocaleResolveInfoContainer();
		BlueskyContextHolder.getProperties(LocaleContextResolverProperties.class).getLocaleResolveHandlerList().forEach(x -> x.setLocaleContext(request, response, localeContext, localeResolveInfoContainer));
		
		request.setAttribute(LOCALE_CONTEXT_REQUEST_ATTRIBUTE_NAME, localeResolveInfoContainer.getLocaleContext());
	}

}
