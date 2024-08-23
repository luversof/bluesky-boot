package io.github.luversof.boot.web.servlet.i18n;

import java.util.Locale;

import org.springframework.beans.factory.BeanNameAware;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * LocaleContextResolver에서 여러 방식의 resolver를 사용하기 위해 제공 
 */
public interface LocaleResolveHandler extends BeanNameAware {

	/**
	 * 요청마다 Locale을 계산
	 * @param request
	 * @param localeResolveInfoContainer
	 */
	void resolveLocale(HttpServletRequest request, LocaleResolveInfoContainer localeResolveInfoContainer);
	
	/**
	 * 유저가 locale 변경 요청하는 경우 처리
	 * @param request
	 * @param response
	 * @param localeContext
	 * @param localeResolveInfoContainer
	 */
	void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale, LocaleResolveInfoContainer localeResolveInfoContainer);

}
