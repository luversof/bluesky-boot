package io.github.luversof.boot.web.servlet.i18n;

import java.util.Comparator;
import java.util.List;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.web.servlet.LocaleContextResolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BlueskyLocaleContextResolver implements LocaleContextResolver {
	
	public static final String LOCALE_CONTEXT_REQUEST_ATTRIBUTE_NAME = BlueskyLocaleContextResolver.class.getName() + ".LOCALE_CONTEXT";
	
	// 모듈별 조회로 변경 예정
	private final List<LocaleResolveHandler> handlerList;
	
	public BlueskyLocaleContextResolver(List<LocaleResolveHandler> handlerList) {
		this.handlerList = handlerList;
		handlerList.sort(Comparator.comparingInt(LocaleResolveHandler::getOrder));
	}

	@Override
	public LocaleContext resolveLocaleContext(HttpServletRequest request) {
		var localeContext = (LocaleContext) request.getAttribute(LOCALE_CONTEXT_REQUEST_ATTRIBUTE_NAME);
		if (localeContext != null) {
			return localeContext;
		}
		
		LocaleResolveInfo localeResolveInfo = new LocaleResolveInfo();
		handlerList.forEach(x -> x.resolveLocaleContext(request, localeResolveInfo));
		
		localeContext = localeResolveInfo.getLocaleContext();
		request.setAttribute(LOCALE_CONTEXT_REQUEST_ATTRIBUTE_NAME, localeContext);
		return localeContext;
	}
	

	@Override
	public void setLocaleContext(HttpServletRequest request, HttpServletResponse response, LocaleContext localeContext) {
		LocaleResolveInfo localeResolveInfo = new LocaleResolveInfo();
		handlerList.forEach(x -> x.setLocaleContext(request, response, localeContext, localeResolveInfo));
		
		request.setAttribute(LOCALE_CONTEXT_REQUEST_ATTRIBUTE_NAME, localeResolveInfo.getLocaleContext());
	}

}
