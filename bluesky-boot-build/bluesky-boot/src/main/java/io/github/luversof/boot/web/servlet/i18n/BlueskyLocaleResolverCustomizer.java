package io.github.luversof.boot.web.servlet.i18n;

import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 복수의 customizer를 사용할 수 있도록 제공
 */
public interface BlueskyLocaleResolverCustomizer {
	
	LocaleResolverResult resolveLocaleContext(HttpServletRequest request, List<LocaleResolverResult> resultList);

}
