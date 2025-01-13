package io.github.luversof.boot.web.servlet.i18n;

import java.util.Locale;

import org.springframework.beans.factory.BeanNameAware;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Provides multiple resolver types to use in LocaleContextResolver
 */
public interface LocaleResolveHandler extends BeanNameAware {

	/**
	 * Resolve locale for each request
	 * 
	 * @param request request
	 * @param localeResolveInfoContainer An object containing the resolved results and final results.
	 */
	void resolveLocale(HttpServletRequest request, LocaleResolveInfoContainer localeResolveInfoContainer);
	
	/**
	 * Method to use when a user requests a locale change
	 * 
	 * @param request request
	 * @param response response
	 * @param locale locale
	 * @param localeResolveInfoContainer An object containing the result set and the final result.
	 */
	void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale, LocaleResolveInfoContainer localeResolveInfoContainer);

}
