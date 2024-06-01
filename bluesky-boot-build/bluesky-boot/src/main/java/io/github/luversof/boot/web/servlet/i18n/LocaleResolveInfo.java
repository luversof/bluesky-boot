package io.github.luversof.boot.web.servlet.i18n;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.springframework.context.i18n.LocaleContext;

import lombok.Data;

@Data
public class LocaleResolveInfo {
	
	private List<LocaleResolveResult> resultList = new ArrayList<>();
	
	/**
	 * 최종적으로 사용할 LocaleResolveResult 제공자
	 */
	private Supplier<LocaleResolveResult> localeResolveResultSupplier;
	
	// 최종 결과 localeContext
	public LocaleContext getLocaleContext() {
		if (localeResolveResultSupplier == null) {
			return () -> null;
		}
		return localeResolveResultSupplier.get().getResolveLocaleContext();
	}
	
}
