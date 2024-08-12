package io.github.luversof.boot.web.servlet.i18n.handler;

import java.util.Locale;

import org.springframework.context.i18n.LocaleContext;
import org.springframework.http.HttpHeaders;

import io.github.luversof.boot.web.servlet.i18n.LocaleContextResolveInfoContainer;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AcceptHeaderLocaleContextResolveHandler extends AbstractLocaleContextResolveHandler {
	
	public static final String DEFAULT_BEAN_NAME = "acceptHeaderLocaleContextResolverHandler";
	
	public AcceptHeaderLocaleContextResolveHandler(int order, String localePropertiesBeanName, String localeContextResolveHandlerPropertiesBeanName) {
		super(order, localePropertiesBeanName, localeContextResolveHandlerPropertiesBeanName);
	}
	
	@Override
	public void resolveLocaleContext(HttpServletRequest request, LocaleContextResolveInfoContainer localeContextResolveInfoContainer) {
		var localeContextResolveInfo = createLocaleContextResolveInfo();
		addLocaleContextResolveInfo(localeContextResolveInfoContainer, localeContextResolveInfo);
		
		// accept header가 있는지 확인
		if (request.getHeader(HttpHeaders.ACCEPT_LANGUAGE) == null) {
			return;
		}
		
		/**
		 * 이거 없으면 defaultLocale 반환으로 알고 있음.
		 */
		Locale requestLocale = request.getLocale();		// parse 된 첫번째 locale 반환함. getLocales() 사용 여부 검토 필요
		localeContextResolveInfo.setRequestLocaleContext(() -> requestLocale);
		
		// 허용된 locale인지 확인
		var localeProperties = getLocaleProperties();
		var enableLocaleList = localeProperties.getEnableLocaleList();
		
		// locale 설정이 없으면 resolve 처리 하지 않음
		if (enableLocaleList.isEmpty()) {
			return;
		}
		
		// 언어만 체크한다거나 하는 식의 옵션을 추가할 수도 있음
		// 완전 일치한 경우가 없으면 언어가 일치하는 경우를 찾는다.
		Locale resolveLocale = (enableLocaleList.contains(requestLocale)) ? requestLocale : enableLocaleList.stream().filter(x -> x.getLanguage().equals(requestLocale.getLanguage())).findFirst().orElseGet(() -> null);

		if (resolveLocale != null) {
			localeContextResolveInfo.setResolveLocaleContext(() -> resolveLocale);
		}
		
		// resolveLocale이 없어도 supplier를 제공하는게 맞는지 확인 필요
		// 만약 앞서 설정된 localeResolveInfoSupplier가 없으면 여기서 구한 requestLocale을 설정함
		if (localeContextResolveInfoContainer.getRepresentativeSupplier() == null) {
			localeContextResolveInfoContainer.setRepresentativeSupplier(() -> localeContextResolveInfo);
		} else {
			// 만약 앞서 구한 supplier가 있으면 어떻게 처리할지 구성
		}
		
	}

	@Override
	public void setLocaleContext(HttpServletRequest request, HttpServletResponse response, LocaleContext localeContext, LocaleContextResolveInfoContainer localeContextResolveInfoContainer) {
		// DO NOTHING
	}

}
