package io.github.luversof.boot.web.servlet.i18n.handler;

import java.util.Locale;
import java.util.TimeZone;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import io.github.luversof.boot.context.BlueskyContextHolder;
import io.github.luversof.boot.web.CookieProperties;
import io.github.luversof.boot.web.servlet.i18n.LocaleResolveInfo;
import io.github.luversof.boot.web.servlet.i18n.LocaleResolveInfoContainer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 설정된 LocaleModuleProperties와 CookieModuleProperties를 기준으로 locale을 처리
 */
@Slf4j
public class CookieLocaleResolveHandler extends AbstractLocaleResolveHandler {
	
	public static final String DEFAULT_BEAN_NAME = "cookieLocaleResolveHandler";
	public static final String EXTERNAL_COOKIE_BEAN_NAME = "externalCookieLocaleResolveHandler";
	
	private final String cookiePropertiesBeanName;
	
	public String getLocaleRequestAttributeName() {
		return cookiePropertiesBeanName + ".LOCALE";
	}
	
	public String getTimeZoneRequestAttributeName() {
		return cookiePropertiesBeanName + ".TIME_ZONE";
	}
	
	public CookieLocaleResolveHandler(String localePropertiesBeanName, String cookiePropertiesBeanName, String localeResolveHandlerPropertiesBeanName) {
		super(localePropertiesBeanName, localeResolveHandlerPropertiesBeanName);
		this.cookiePropertiesBeanName = cookiePropertiesBeanName;
	}
	
	@Override
	public void resolveLocale(HttpServletRequest request, LocaleResolveInfoContainer localeResolveInfoContainer) {
		var localeResolveInfo = createLocaleResolveInfo();
		var requestLocale = getRequestLocale(request);
		localeResolveInfo.setRequestLocale(requestLocale);
		
		setResolveLocale(localeResolveInfo, localeResolveInfoContainer);
		
		var localeResolveHandlerProperties = getLocaleResolveHandlerProperties();
		var localeResolveInfoCondition = localeResolveHandlerProperties.getLocaleResolveInfoCondition();
		var requestAttributes = RequestContextHolder.getRequestAttributes();
		if (localeResolveInfoCondition != null && localeResolveInfoCondition.isResolveLocaleCookieCreate() && requestAttributes != null) {
			HttpServletResponse response = ((ServletRequestAttributes) requestAttributes).getResponse();
			createCookie(response, localeResolveInfo);
		}
		
		setRepresenatativeSupplier(localeResolveInfoContainer, localeResolveInfo);
		addLocaleResolveInfo(localeResolveInfoContainer, localeResolveInfo);
	}

	@Override
	public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale, LocaleResolveInfoContainer localeResolveInfoContainer) {
		var localeResolveInfo = createLocaleResolveInfo();
		// 해당 locale이 허용되었는지 체크하는 과정
		localeResolveInfo.setRequestLocale(locale);
		setResolveLocale(localeResolveInfo, localeResolveInfoContainer);

		var localeResolveHandlerProperties = getLocaleResolveHandlerProperties();
		var localeResolveInfoCondition = localeResolveHandlerProperties.getLocaleResolveInfoCondition();
		if (localeResolveInfoCondition != null && localeResolveInfoCondition.isSetLocaleCookieCreate()) {
			createCookie(response, localeResolveInfo);
		}
		
		setRepresenatativeSupplier(localeResolveInfoContainer, localeResolveInfo);
		addLocaleResolveInfo(localeResolveInfoContainer, localeResolveInfo);
	}
	
	/**
	 * request cookie에서 Locale을 구함
	 * @param request
	 */
	private Locale getRequestLocale(HttpServletRequest request) {
		if (request.getAttribute(getLocaleRequestAttributeName()) != null) {
			return (Locale) request.getAttribute(getLocaleRequestAttributeName());
		}
		
		// 쿠키 조회
		var cookieProperties = getCookieProperties();
		String cookieName = cookieProperties.getName();
		if (cookieName == null) {
			return null;
		}
		
		var cookie = WebUtils.getCookie(request, cookieName);
		if (cookie == null) {
			return null;
		}
		
		Locale locale = null;
		TimeZone timeZone = null;
		
		String value = cookie.getValue();
		String localePart = value;
		String timeZonePart = null;
		int separatorIndex = localePart.indexOf('/');
		if (separatorIndex == -1) {
			// Leniently accept older cookies separated by a space...
			separatorIndex = localePart.indexOf(' ');
		}
		if (separatorIndex >= 0) {
			localePart = value.substring(0, separatorIndex);
			timeZonePart = value.substring(separatorIndex + 1);
		}
		try {
			locale = (!"-".equals(localePart) ? StringUtils.parseLocale(localePart) : null);
			if (timeZonePart != null) {
				timeZone = StringUtils.parseTimeZoneString(timeZonePart);
			}
		}
		catch (IllegalArgumentException ex) {
			log.debug("Ignoring invalid locale cookie '" + cookieName + "': [" + value + "] due to: " + ex.getMessage());
		}
		
		if (locale == null) {
			return null;
		}
		
		request.setAttribute(getLocaleRequestAttributeName(), locale);
		request.setAttribute(getTimeZoneRequestAttributeName(), timeZone);
		return locale;
	}
	
	private void createCookie(HttpServletResponse response, LocaleResolveInfo localeResolveResult) {
		if (response == null) {
			log.debug("response is null");
			return;
		}
		
		// resolveLocale을 기준으로 쿠키를 굽는 처리
		// 변경을 요청한 경우이므로 무조건 구우면 된다.
		var resolveLocale = localeResolveResult.getResolveLocale();
		if (resolveLocale == null) {
			return;
		}
		
		var cookieProperties = getCookieProperties();
		
		var cookie = ResponseCookie
			.from(cookieProperties.getName(), resolveLocale.toLanguageTag())
			.maxAge(cookieProperties.getMaxAge())
			.domain(cookieProperties.getDomain())
			.path(cookieProperties.getPath())
			.secure(Boolean.TRUE.equals(cookieProperties.getSecure()))
			.httpOnly(Boolean.TRUE.equals(cookieProperties.getHttpOnly()))
			.sameSite(cookieProperties.getSameSite())
			.build();
		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
	}

	protected CookieProperties getCookieProperties() {
		return BlueskyContextHolder.getProperties(CookieProperties.class, cookiePropertiesBeanName);
	}

}
