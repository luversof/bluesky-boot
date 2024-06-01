package io.github.luversof.boot.web.servlet.i18n;

import java.util.Comparator;
import java.util.List;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.web.servlet.LocaleContextResolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BlueskyLocaleContextResolver implements LocaleContextResolver {
	
	private final List<LocaleResolverHandler> handlerList;
	
	private final Comparator<LocaleResolverHandler> comparator = (o1, o2) -> o1.getOrder() - o2.getOrder();
	
	public BlueskyLocaleContextResolver(List<LocaleResolverHandler> handlerList) {
		this.handlerList = handlerList;
		handlerList.sort(comparator);
	}

	@Override
	public LocaleContext resolveLocaleContext(HttpServletRequest request) {
		LocaleResolveInfo localeResolveInfo = new LocaleResolveInfo();
		handlerList.forEach(x -> x.resolveLocaleContext(request, localeResolveInfo));
		return localeResolveInfo.getLocaleContext();
	}

	@Override
	public void setLocaleContext(HttpServletRequest request, HttpServletResponse response, LocaleContext localeContext) {
		LocaleResolveInfo localeResolveInfo = new LocaleResolveInfo();
		handlerList.forEach(x -> x.setLocaleContext(request, response, localeContext, localeResolveInfo));
	}

}
