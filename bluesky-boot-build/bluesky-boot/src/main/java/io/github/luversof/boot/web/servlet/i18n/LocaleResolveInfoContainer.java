package io.github.luversof.boot.web.servlet.i18n;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.springframework.context.i18n.LocaleContext;

import io.github.luversof.boot.context.BlueskyContextHolder;
import io.github.luversof.boot.context.i18n.LocaleProperties;
import lombok.Data;

@Data
public class LocaleResolveInfoContainer {
	
	private List<LocaleResolveInfo> localeResolveInfoList = new ArrayList<>();
	
	/**
	 * 최종적으로 사용할 LocaleResolveInfo 제공자
	 */
	private Supplier<LocaleResolveInfo> representativeLocaleResolveInfoSupplier;
	
	// 최종 결과 localeContext
	public LocaleContext getLocaleContext() {
		if (representativeLocaleResolveInfoSupplier == null) {
			// 없으면 기본 설정한 locale이 있는지 확인
			return () -> BlueskyContextHolder.getProperties(LocaleProperties.class, LocaleProperties.DEFAULT_BEAN_NAME).getDefaultLocale();
		}
		return representativeLocaleResolveInfoSupplier.get().getResolveLocaleContext();
	}
	
	public Optional<LocaleResolveInfo> findByHandlerBeanName(String handlerBeanName) {
		return localeResolveInfoList.stream().filter(x -> x.getHandlerBeanName().equals(handlerBeanName)).findFirst();
	}
	
}
