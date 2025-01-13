package io.github.luversof.boot.web.servlet.i18n.handler;

import java.util.Locale;

import org.springframework.http.HttpHeaders;

import io.github.luversof.boot.web.servlet.i18n.LocaleResolveInfoContainer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Handler for resolving the locale requested by AcceptHeader
 */
public class AcceptHeaderLocaleResolveHandler extends AbstractLocaleResolveHandler {
	
	public static final String DEFAULT_BEAN_NAME = "acceptHeaderLocaleResolverHandler";
	
	public AcceptHeaderLocaleResolveHandler(String localePropertiesBeanName, String localeResolveHandlerPropertiesBeanName) {
		super(localePropertiesBeanName, localeResolveHandlerPropertiesBeanName);
	}
	
	@Override
	public void resolveLocale(HttpServletRequest request, LocaleResolveInfoContainer localeResolveInfoContainer) {
		var localeResolveInfo = createLocaleResolveInfo();
		var requestLocale = getRequestLocale(request);
		localeResolveInfo.setRequestLocale(requestLocale);
		
		setResolveLocale(localeResolveInfo, localeResolveInfoContainer);
		
		setRepresenatativeSupplier(localeResolveInfoContainer, localeResolveInfo);
		addLocaleResolveInfo(localeResolveInfoContainer, localeResolveInfo);
	}
	
	/**
	 * request accept header에서 Locale을 구함
	 * 현재는 단일 Locale 반환으로 처리하고 있는데 만약 복수 처리가 필요하다면 관련하여 request.getLocales() 를 사용하는 처리로 개편 필요
	 * @param request
	 * @return
	 */
	private Locale getRequestLocale(HttpServletRequest request) {
		// accept header가 있는지 확인
		if (request.getHeader(HttpHeaders.ACCEPT_LANGUAGE) == null) {
			return null;
		}
		
		return request.getLocale();	// accept language header의 parse 된 첫번째 locale 반환. header가 없으면 Locale.getDefault() 반환
	}

	@Override
	public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale, LocaleResolveInfoContainer localeResolveInfoContainer) {
		// DO NOTHING
	}

}
