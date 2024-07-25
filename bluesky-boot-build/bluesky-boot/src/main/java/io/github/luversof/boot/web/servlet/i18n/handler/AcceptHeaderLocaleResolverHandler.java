package io.github.luversof.boot.web.servlet.i18n.handler;

import java.util.Locale;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.http.HttpHeaders;

import io.github.luversof.boot.context.BlueskyContextHolder;
import io.github.luversof.boot.context.i18n.LocaleProperties;
import io.github.luversof.boot.web.servlet.i18n.LocaleResolveInfo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AcceptHeaderLocaleResolverHandler extends AbstractLocaleResolverHandler {
	
	public AcceptHeaderLocaleResolverHandler(int order, String localePropertiesBeanName) {
		super(order, localePropertiesBeanName);
	}
	
	@Override
	public void resolveLocaleContext(HttpServletRequest request, LocaleResolveInfo localeResolveInfo) {
		var localeResolveResult = createLocaleResolveResult(localeResolveInfo);
		// accept header가 있는지 확인
		if (request.getHeader(HttpHeaders.ACCEPT_LANGUAGE) == null) {
			return;
		}
		
		/**
		 * 이거 없으면 defaultLocale 반환으로 알고 있음.
		 */
		Locale requestLocale = request.getLocale();
		localeResolveResult.setRequestLocaleContext(() -> requestLocale);
		
		// 허용된 locale인지 확인
		var localeProperties = BlueskyContextHolder.getProperties(LocaleProperties.class, getLocalePropertiesBeanName());
		var enableLocaleList = localeProperties.getEnableLocaleList();
		
		// locale 설정이 없으면 resolve 처리 하지 않음
		if (enableLocaleList.isEmpty()) {
			return;
		}
		
		// 언어만 체크한다거나 하는 식의 옵션을 추가할 수도 있음
		// 완전 일치한 경우가 없으면 언어가 일치하는 경우를 찾는다.
		Locale resolveLocale = (enableLocaleList.contains(requestLocale)) ? requestLocale : enableLocaleList.stream().filter(x -> x.getLanguage().equals(requestLocale.getLanguage())).findFirst().orElseGet(() -> null);

		if (resolveLocale != null) {
			localeResolveResult.setResolveLocaleContext(() -> resolveLocale);
		}
		
		// 만약 앞서 설정된 localeResolveResultSupplier가 있으면 여기서 구한 requestLocale을 설정함
		if (localeResolveInfo.getLocaleResolveResultSupplier() == null) {
			localeResolveInfo.setLocaleResolveResultSupplier(() -> localeResolveResult);
		}
		
	}

	@Override
	public void setLocaleContext(HttpServletRequest request, HttpServletResponse response, LocaleContext localeContext, LocaleResolveInfo localeResolveInfo) {
		// DO NOTHING
	}

}
