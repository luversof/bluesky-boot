package io.github.luversof.boot.web.servlet.i18n;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.context.i18n.LocaleContext;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * LocaleContextResolver에서 여러 방식의 resolver를 사용하기 위해 제공 
 */
public interface LocaleResolveHandler extends BeanNameAware {
	
	int getOrder();
	
	void resolveLocaleContext(HttpServletRequest request, LocaleResolveInfo localeResolveInfo);
	
	void setLocaleContext(HttpServletRequest request, HttpServletResponse response, LocaleContext localeContext, LocaleResolveInfo localeResolveInfo);

}
