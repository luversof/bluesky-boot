package io.github.luversof.boot.web.servlet.i18n.handler;

import java.util.Locale;
import java.util.TimeZone;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.TimeZoneAwareLocaleContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import io.github.luversof.boot.context.BlueskyContextHolder;
import io.github.luversof.boot.web.CookieProperties;
import io.github.luversof.boot.web.LocaleContextResolveHandlerProperties.UsePreLocaleContextResolveInfoCondition;
import io.github.luversof.boot.web.servlet.i18n.LocaleContextResolveInfoContainer;
import io.github.luversof.boot.web.servlet.i18n.LocaleContextResolveInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 설정된 LocaleModuleProperties와 CookieModuleProperties를 기준으로 locale을 처리
 */
@Slf4j
public class CookieLocaleContextResolveHandler extends AbstractLocaleContextResolveHandler {
	
	private final String cookiePropertiesBeanName;
	
	public CookieLocaleContextResolveHandler(int order, String localePropertiesBeanName, String cookiePropertiesBeanName, String localeContextResolveHandlerPropertiesBeanName) {
		super(order, localePropertiesBeanName, localeContextResolveHandlerPropertiesBeanName);
		this.cookiePropertiesBeanName = cookiePropertiesBeanName;
	}
	
	@Override
	public void resolveLocaleContext(HttpServletRequest request, LocaleContextResolveInfoContainer localeContextResolveInfoContainer) {
		var localeContextResolveInfo = createLocaleContextResolveInfo();
		var requestLocaleContext = getRequestLocaleContext(request);
		localeContextResolveInfo.setRequestLocaleContext(requestLocaleContext);
		
		setResolveLocaleContext(localeContextResolveInfo, localeContextResolveInfoContainer);
		
		var localeContextResolveHandlerProperties = getLocaleContextResolveHandlerProperties();
		if (Boolean.TRUE.equals(localeContextResolveHandlerProperties.getResolveLocaleContextCookieCreate())) {
			HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
			createCookie(response, localeContextResolveInfo);
		}
		
		setRepresenatativeSupplier(localeContextResolveInfoContainer, localeContextResolveInfo);
		addLocaleContextResolveInfo(localeContextResolveInfoContainer, localeContextResolveInfo);
	}

	@Override
	public void setLocaleContext(HttpServletRequest request, HttpServletResponse response, LocaleContext localeContext, LocaleContextResolveInfoContainer localeContextResolveInfoContainer) {
		var localeContextResolveInfo = createLocaleContextResolveInfo();
		// 해당 locale이 허용되었는지 체크하는 과정
		localeContextResolveInfo.setRequestLocaleContext(localeContext);
		setResolveLocaleContext(localeContextResolveInfo, localeContextResolveInfoContainer);

		var localeContextResolveHandlerProperties = getLocaleContextResolveHandlerProperties();
		if (Boolean.TRUE.equals(localeContextResolveHandlerProperties.getSetLocaleContextCookieCreate())) {
			createCookie(response, localeContextResolveInfo);
		}
		
		setRepresenatativeSupplier(localeContextResolveInfoContainer, localeContextResolveInfo);
		addLocaleContextResolveInfo(localeContextResolveInfoContainer, localeContextResolveInfo);
	}
	
	
	/**
	 * request에서 Locale을 구함
	 * @param request
	 * @param localeContextResolveInfo
	 */
	private LocaleContext getRequestLocaleContext(HttpServletRequest request) {
		
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
		
		Locale requestLocale = locale;
		TimeZone requestTimezone = timeZone;
		return new TimeZoneAwareLocaleContext() {

			@Override
			public Locale getLocale() {
				return requestLocale;
			}

			@Override
			public TimeZone getTimeZone() {
				return requestTimezone;
			}
			
		};
	}
	
	// 언어만 체크하는 등의 추가 조건이 있을 수 있음
	private void setResolveLocaleContext(LocaleContextResolveInfo localeContextResolveInfo, LocaleContextResolveInfoContainer localeContextResolveInfoContainer) {
		var localeProperties = getLocaleProperties();
		if (localeProperties.getEnableLocaleList().isEmpty()) {
			return;
		}
		
		// locale이 단일이면 해당 로케일로 지정 처리
		if (localeProperties.getEnableLocaleList().size() == 1) {
			localeContextResolveInfo.setResolveLocaleContext(localeProperties::getDefaultLocale);
			return;
		}
		
		var localeContextResolveHandlerProperties = getLocaleContextResolveHandlerProperties();
		var usePreLocaleContextResolveInfoCondition = localeContextResolveHandlerProperties.getUsePreLocaleContextResolveInfoCondition();
		
		Locale resolveLocale = null;
		// 선행 handler resolveLocale을 먼저 참고하는 경우
		if (UsePreLocaleContextResolveInfoCondition.USE_FIRST.equals(usePreLocaleContextResolveInfoCondition) || UsePreLocaleContextResolveInfoCondition.USE_LANGUAGE_FIRST.equals(usePreLocaleContextResolveInfoCondition)) {
			resolveLocale = getResolveLocaleByPreResolveInfo(localeContextResolveInfoContainer);
		}
		
		if (resolveLocale != null) {
			var targetResolveLocale = resolveLocale;
			localeContextResolveInfo.setResolveLocaleContext(() -> targetResolveLocale);
			return;
		}
		
		// 내 자신 로케일 계산하고 있으면 해당 설정
		var requestLocaleContext = localeContextResolveInfo.getRequestLocaleContext();
		var requestLocale = requestLocaleContext == null ? null : requestLocaleContext.getLocale();
		resolveLocale = getResolveLocale(requestLocale, false);
		if (resolveLocale != null) {
			var targetResolveLocale = resolveLocale;
			localeContextResolveInfo.setResolveLocaleContext(() -> targetResolveLocale);
			return;
		}
		
		// 자신의 resolveLocale이 없으면 선행 로케일 참조하는지 체크 
		if (UsePreLocaleContextResolveInfoCondition.USE_WHEN_NOT_RESOLVED.equals(usePreLocaleContextResolveInfoCondition) || UsePreLocaleContextResolveInfoCondition.USE_LANGUAGE_WHEN_NOT_RESOLVED.equals(usePreLocaleContextResolveInfoCondition)) {
			resolveLocale = getResolveLocaleByPreResolveInfo(localeContextResolveInfoContainer);
		}
		
		if (resolveLocale != null) {
			var targetResolveLocale = resolveLocale;
			localeContextResolveInfo.setResolveLocaleContext(() -> targetResolveLocale);
			return;
		}
		// 최종적으로 resolveLocale이 없는 경우 굳이 설정하지 않음
	}
	
	private void createCookie(HttpServletResponse response, LocaleContextResolveInfo localeContextResolveResult) {
		// resolveLocale을 기준으로 쿠키를 굽는 처리
		// 변경을 요청한 경우이므로 무조건 구우면 된다.
		var resolveLocaleContext = localeContextResolveResult.getResolveLocaleContext();
		if (resolveLocaleContext == null) {
			return;
		}
		
		var resolveLocale = resolveLocaleContext.getLocale();
		if (resolveLocale == null) {
			return;
		}
		
		var cookieProperties = getCookieProperties();
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

	protected CookieProperties getCookieProperties() {
		return BlueskyContextHolder.getProperties(CookieProperties.class, cookiePropertiesBeanName);
	}

}
