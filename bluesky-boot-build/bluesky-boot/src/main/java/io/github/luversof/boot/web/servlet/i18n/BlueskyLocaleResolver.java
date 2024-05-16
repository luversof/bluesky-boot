package io.github.luversof.boot.web.servlet.i18n;

import java.util.Comparator;
import java.util.List;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.web.servlet.LocaleContextResolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BlueskyLocaleResolver implements LocaleContextResolver {
	
	private List<BlueskyLocaleResolverHandler> handlerList;
	
	private Comparator<BlueskyLocaleResolverHandler> comparator = (o1, o2) -> o1.getOrder() - o2.getOrder();

	@Override
	public LocaleContext resolveLocaleContext(HttpServletRequest request) {
		LocaleResolveResultInfo localeResolveResultInfo = new LocaleResolveResultInfo();
		handlerList.sort(comparator);
		handlerList.forEach(x -> x.resolveLocaleContext(request, localeResolveResultInfo));
		return localeResolveResultInfo.getLocaleContext();
	}

	@Override
	public void setLocaleContext(HttpServletRequest request, HttpServletResponse response, LocaleContext localeContext) {
		LocaleResolveResultInfo localeResolveResultInfo = new LocaleResolveResultInfo();
		handlerList.sort(comparator);
		handlerList.forEach(x -> x.setLocaleContext(request, response, localeContext, localeResolveResultInfo));
	}

}
