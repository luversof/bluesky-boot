package io.github.luversof.boot.web.servlet.i18n;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.context.i18n.LocaleContext;

import io.github.luversof.boot.context.BlueskyContextHolder;
import io.github.luversof.boot.context.i18n.LocaleProperties;
import lombok.Data;

@Data
public class LocaleContextResolveInfoContainer {
	
	private List<LocaleContextResolveInfo> resolveList = new ArrayList<>();
	
	/**
	 * 최종적으로 사용할 LocaleResolveInfo 제공자
	 */
	private Supplier<LocaleContextResolveInfo> representativeSupplier;
	
	// 최종 결과 localeContext
	public LocaleContext getLocaleContext() {
		if (representativeSupplier == null) {
			return this::getDefaultLocale;
		}
		
		var resolveLocaleContext = representativeSupplier.get().getResolveLocaleContext();
		if (resolveLocaleContext == null) {
			return this::getDefaultLocale;
		}
		return resolveLocaleContext;
	}
	
	private Locale getDefaultLocale() {
		return BlueskyContextHolder.getProperties(LocaleProperties.class, LocaleProperties.DEFAULT_BEAN_NAME).getDefaultLocale();
	}
	
	public Optional<LocaleContextResolveInfo> findByHandlerBeanName(String handlerBeanName) {
		return resolveList.stream().filter(x -> x.getHandlerBeanName().equals(handlerBeanName)).findFirst();
	}
	
}
