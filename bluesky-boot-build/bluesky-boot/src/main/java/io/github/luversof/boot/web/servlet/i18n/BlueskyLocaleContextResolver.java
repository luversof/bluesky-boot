package io.github.luversof.boot.web.servlet.i18n;

import java.util.Comparator;
import java.util.List;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.web.servlet.LocaleContextResolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BlueskyLocaleContextResolver implements LocaleContextResolver {
	
	private List<LocaleResolverHandler> handlerList;
	
	private Comparator<LocaleResolverHandler> comparator = (o1, o2) -> o1.getOrder() - o2.getOrder();

	@Override
	public LocaleContext resolveLocaleContext(HttpServletRequest request) {
		LocaleResolveInfo localeContextResolveInfo = new LocaleResolveInfo();
		handlerList.sort(comparator);
		handlerList.forEach(x -> x.resolveLocaleContext(request, localeContextResolveInfo));
		return localeContextResolveInfo.getLocaleContext();
	}

	@Override
	public void setLocaleContext(HttpServletRequest request, HttpServletResponse response, LocaleContext localeContext) {
		LocaleResolveInfo localeContextResolveInfo = new LocaleResolveInfo();
		handlerList.sort(comparator);
		handlerList.forEach(x -> x.setLocaleContext(request, response, localeContext, localeContextResolveInfo));
	}

}
