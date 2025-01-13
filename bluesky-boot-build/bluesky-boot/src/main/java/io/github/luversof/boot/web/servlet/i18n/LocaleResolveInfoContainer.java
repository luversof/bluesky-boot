package io.github.luversof.boot.web.servlet.i18n;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

import io.github.luversof.boot.context.BlueskyContextHolder;
import io.github.luversof.boot.context.i18n.LocaleProperties;
import lombok.Data;

/**
 * Container containing locale information to use
 */
@Data
public class LocaleResolveInfoContainer {
	
	private List<LocaleResolveInfo> resolveList = new ArrayList<>();
	
	/**
	 * 최종적으로 사용할 LocaleResolveInfo 제공자
	 */
	private Supplier<LocaleResolveInfo> representativeSupplier;
	
	/**
	 * 최종 결과 localeContext
	 * representativeSupplier가 지정되지 않으면 defaultLocale을 사용
	 * @return
	 */
	public Locale getLocale() {
		if (representativeSupplier == null) {
			return getDefaultLocale();
		}
		
		var resolveLocale = representativeSupplier.get().getResolveLocale();
		return resolveLocale == null ? getDefaultLocale() : resolveLocale;
	}
	
	private Locale getDefaultLocale() {
		return BlueskyContextHolder.getProperties(LocaleProperties.class, LocaleProperties.DEFAULT_BEAN_NAME).getDefaultLocale();
	}
	
}
