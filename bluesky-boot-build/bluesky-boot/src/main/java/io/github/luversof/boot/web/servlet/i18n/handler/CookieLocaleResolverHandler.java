package io.github.luversof.boot.web.servlet.i18n.handler;

import java.util.Locale;
import java.util.TimeZone;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

import io.github.luversof.boot.context.BlueskyContextHolder;
import io.github.luversof.boot.context.i18n.LocaleProperties;
import io.github.luversof.boot.web.CookieProperties;
import io.github.luversof.boot.web.servlet.i18n.LocaleContextResolveInfoContainer;
import io.github.luversof.boot.web.servlet.i18n.LocaleContextResolveInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 설정된 LocaleModuleProperties와 CookieModuleProperties를 기준으로 locale을 처리
 */
@Slf4j
public class CookieLocaleResolverHandler extends AbstractLocaleResolverHandler {
	
	private final String cookiePropertiesBeanName;
	
	public CookieLocaleResolverHandler(int order, String localePropertiesBeanName, String cookiePropertiesBeanName, String localeContextResolveHandlerPropertiesBeanName) {
		super(order, localePropertiesBeanName, localeContextResolveHandlerPropertiesBeanName);
		this.cookiePropertiesBeanName = cookiePropertiesBeanName;
	}
	
	@Override
	public void resolveLocaleContext(HttpServletRequest request, LocaleContextResolveInfoContainer localeContextResolveInfoContainer) {
		var currentLocaleResolveInfo = createLocaleResolveInfo(localeContextResolveInfoContainer);
		setRequestLocaleContext(request, currentLocaleResolveInfo);
		setResolveLocaleContext(localeContextResolveInfoContainer, currentLocaleResolveInfo);
		
		// do something
		// requestLocale과 resolveLocale이 다르거나 혹은 resolveLocale을 기준으로 쿠키를 굽거나 기존 쿠키를 삭제하는 등의 처리?
	}

	@Override
	public void setLocaleContext(HttpServletRequest request, HttpServletResponse response, LocaleContext localeContext, LocaleContextResolveInfoContainer localeContextResolveInfoContainer) {
		var currentLocaleResolveInfo = createLocaleResolveInfo(localeContextResolveInfoContainer);
		currentLocaleResolveInfo.setRequestLocaleContext(localeContext);
		setResolveLocaleContext(localeContextResolveInfoContainer, currentLocaleResolveInfo);

		createCookie(response, currentLocaleResolveInfo);
	}
	
	/**
	 * 요청된 Locale을 구한다.
	 * @param request
	 * @param localeContextResolveInfo
	 */
	private void setRequestLocaleContext(HttpServletRequest request, LocaleContextResolveInfo localeContextResolveInfo) {
		
		// 쿠키 조회
		CookieProperties cookieProperties = BlueskyContextHolder.getProperties(CookieProperties.class);
		String cookieName = cookieProperties.getName();
		if (cookieName == null) {
			return;
		}
		
		var cookie = WebUtils.getCookie(request, cookieName);
		if (cookie == null) {
			return;
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
			return;
		}
		
		Locale requestLocale = locale;
		TimeZone requestTimezone = timeZone;
		localeContextResolveInfo.setRequestLocaleContext(new TimeZoneAwareLocaleContext() {

			@Override
			public Locale getLocale() {
				return requestLocale;
			}

			@Override
			public TimeZone getTimeZone() {
				return requestTimezone;
			}
			
		});
	}
	
	// 언어만 체크하는 등의 추가 조건이 있을 수 있음
	private void setResolveLocaleContext(LocaleContextResolveInfoContainer localeContextResolveInfoContainer, LocaleContextResolveInfo localeContextResolveInfo) {
		var localeProperties = BlueskyContextHolder.getProperties(LocaleProperties.class, getLocalePropertiesBeanName());
		if (localeProperties.getEnableLocaleList().isEmpty()) {
			return;
		}
		
		// LocaleContext 제공자
		localeContextResolveInfoContainer.setRepresentativeSupplier(() -> localeContextResolveInfo);
		
		var requestLocaleContext = localeContextResolveInfo.getRequestLocaleContext();
		if (requestLocaleContext == null) {
			// 요청 localeContext가 없어도 이전 localeResolverHandler에서 계산된 resolve LocaleContext가 있으면 해당 기준으로 처리하는 경우가 있을 수 있음
			
			localeContextResolveInfo.setResolveLocaleContext(localeProperties::getDefaultLocale);
			return;
		}
		
		var requestLocale = requestLocaleContext.getLocale();
		
		// 현재 계산된 requestLocaleContext도 있고, 이전 localeResolverHandler에서 계산된 localeContext 도 있는 경우 
		
		if (localeProperties.getEnableLocaleList().contains(requestLocale)) {
			localeContextResolveInfo.setResolveLocaleContext(() -> requestLocale);
		} else {
			// 해당하는 locale이 없으면 default Locale을 설정해야 할듯?
			localeContextResolveInfo.setResolveLocaleContext(localeProperties::getDefaultLocale);
		}
		
		
	}
	
	private void createCookie(HttpServletResponse response, LocaleContextResolveInfo localeContextResolveInfo) {
		// resolveLocale을 기준으로 쿠키를 굽는 처리
		// 변경을 요청한 경우이므로 무조건 구우면 된다.
		var resolveLocaleContext = localeContextResolveInfo.getResolveLocaleContext();
		if (resolveLocaleContext == null) {
			return;
		}
		
		var resolveLocale = resolveLocaleContext.getLocale();
		if (resolveLocale == null) {
			return;
		}
		
		CookieProperties cookieProperties = BlueskyContextHolder.getProperties(CookieProperties.class, cookiePropertiesBeanName);
		// cookie 생성여부 확인 후 쿠키 생성
		if (!Boolean.TRUE.equals(cookieProperties.getEnabled())) {
			return;
		}
		
		var cookie = ResponseCookie
			.from(cookieProperties.getName(), resolveLocale.toLanguageTag())
			.maxAge(cookieProperties.getMaxAge())
			.domain(cookieProperties.getDomain())
			.path(cookieProperties.getPath())
			.secure(Boolean.TRUE.equals(cookieProperties.getSecure()))
			.httpOnly(Boolean.TRUE.equals(cookieProperties.getHttpOnly()))
			// sameSite 설정은 필요시 추가예정 (http에서만 쓰이는 듯함?)
			.build();
		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
	}



}
