package io.github.luversof.boot.web.servlet.i18n;

import org.springframework.context.i18n.LocaleContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 복수의 customizer를 사용할 수 있도록 제공
 */
public interface LocaleResolverHandler {
	
	int getOrder();
	
	void resolveLocaleContext(HttpServletRequest request, LocaleResolveInfo localeResolveInfo);
	
	void setLocaleContext(HttpServletRequest request, HttpServletResponse response, LocaleContext localeContext, LocaleResolveInfo localeResolveInfo);

}
