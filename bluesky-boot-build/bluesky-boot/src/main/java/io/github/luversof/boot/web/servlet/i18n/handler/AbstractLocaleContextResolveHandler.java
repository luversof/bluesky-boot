package io.github.luversof.boot.web.servlet.i18n.handler;

import io.github.luversof.boot.web.servlet.i18n.LocaleContextResolveInfo;
import io.github.luversof.boot.web.servlet.i18n.LocaleContextResolveInfoContainer;

import java.util.Locale;

import org.springframework.util.CollectionUtils;

import io.github.luversof.boot.context.BlueskyContextHolder;
import io.github.luversof.boot.context.i18n.LocaleProperties;
import io.github.luversof.boot.web.LocaleContextResolveHandlerProperties;
import io.github.luversof.boot.web.LocaleContextResolveHandlerProperties.SetRepresentativeCondition;
import io.github.luversof.boot.web.servlet.i18n.LocaleContextResolveHandler;
import lombok.Getter;

public abstract class AbstractLocaleContextResolveHandler implements LocaleContextResolveHandler {
	
	/**
	 *  실행할 순서
	 */
	@Getter
	private final int order;
	
	@Getter
	private final String localePropertiesBeanName;
	
	@Getter
	private final String localeContextResolveHandlerPropertiesBeanName;

	/**
	 * 등록된 localeReolverHandler bean Name
	 */
	@Getter
	private String handlerBeanName;
	
	AbstractLocaleContextResolveHandler(int order, String localePropertiesBeanName, String localeContextResolveHandlerPropertiesBeanName) {
		this.order = order;
		this.localePropertiesBeanName = localePropertiesBeanName;
		this.localeContextResolveHandlerPropertiesBeanName = localeContextResolveHandlerPropertiesBeanName;
	}
	
	@Override
	public void setBeanName(String name) {
		this.handlerBeanName = name;
	}
	
	protected LocaleProperties getLocaleProperties() {
		return BlueskyContextHolder.getProperties(LocaleProperties.class, localePropertiesBeanName);
	}
	
	protected LocaleContextResolveHandlerProperties getLocaleContextResolveHandlerProperties() {
		return BlueskyContextHolder.getProperties(LocaleContextResolveHandlerProperties.class, localeContextResolveHandlerPropertiesBeanName);
	}
	
	/**
	 * 해당 handler에서 사용할 LocaleResolveInfo 생성
	 * @param localeContextResolveInfoContainer
	 * @return
	 */
	protected LocaleContextResolveInfo createLocaleContextResolveInfo() {
		var localeContextResolveInfo = new LocaleContextResolveInfo();
		localeContextResolveInfo.setOrder(getOrder());
		localeContextResolveInfo.setHandlerBeanName(getHandlerBeanName());
		return localeContextResolveInfo;
	}
	
	/**
	 * localeContextResolveInfoContainer에 localeContextResolveInfo를 추가하고 대표 locale 설정 
	 */
	protected void addLocaleContextResolveInfo(LocaleContextResolveInfoContainer localeContextResolveInfoContainer, LocaleContextResolveInfo localeContextResolveInfo) {
		localeContextResolveInfoContainer.getResolveList().add(localeContextResolveInfo);
	}
	
	/**
	 * 대표 로케일 지정 설정
	 * @param localeContextResolveInfoContainer
	 * @param localeContextResolveInfo
	 */
	protected void setRepresenatativeSupplier(LocaleContextResolveInfoContainer localeContextResolveInfoContainer, LocaleContextResolveInfo localeContextResolveInfo) {
		var localeContextResolveHandlerProperties = getLocaleContextResolveHandlerProperties();
		var setRepresentativeCondition = localeContextResolveHandlerProperties.getSetRepresentativeCondition();
		
		switch(setRepresentativeCondition == null ? SetRepresentativeCondition.NONE : setRepresentativeCondition) {
		case NONE:
			break;
		case OVERWRITE:
			localeContextResolveInfoContainer.setRepresentativeSupplier(() -> localeContextResolveInfo);
			break;
		case OVERWRITE_IF_NOT_EXISTS:
			if (localeContextResolveInfoContainer.getRepresentativeSupplier() == null || localeContextResolveInfoContainer.getRepresentativeSupplier().get().getResolveLocaleContext() == null) {
				localeContextResolveInfoContainer.setRepresentativeSupplier(() -> localeContextResolveInfo);
			}
			break;
		default:
			break;
		}
	}

	/**
	 * 대상 Locale을 기준으로 현재 설정된 locale을 가져옴
	 * @param requestLocale
	 */
	protected Locale getResolveLocale(Locale targetLocale, boolean checkLanguageMatchOnly) {
		if (targetLocale == null) {
			return null;
		}
		
		var localeProperties = getLocaleProperties();
		var enableLocaleList = localeProperties.getEnableLocaleList();
		
		if (CollectionUtils.isEmpty(enableLocaleList)) {
			return null;
		}
		
		if (enableLocaleList.contains(targetLocale)) {
			return targetLocale;
		}
		
		if (checkLanguageMatchOnly) {
			for (var enableLocale : enableLocaleList) {
				if (targetLocale.getLanguage().equals(enableLocale.getLanguage())) {
					return enableLocale;
				}
			}
		}
			
		return null;
	}

	/**
	 * 선행 handler에서 계산된 locale을 기준으로 resolveLocale 계산
	 * @param localeContextResolveInfoContainer
	 * @return
	 */
	protected Locale getResolveLocaleByPreResolveInfo(LocaleContextResolveInfoContainer localeContextResolveInfoContainer) {
		var resolveList = localeContextResolveInfoContainer.getResolveList();
		var preResolveLocaleContext = CollectionUtils.isEmpty(resolveList) ? null : resolveList.getLast().getResolveLocaleContext();
		var preResolveLocale = preResolveLocaleContext == null ? null : preResolveLocaleContext.getLocale();
		
		var localeContextResolveHandlerProperties = getLocaleContextResolveHandlerProperties();
		var usePreLocaleContextResolveInfoCondition = localeContextResolveHandlerProperties.getUsePreLocaleContextResolveInfoCondition();
		
		if (preResolveLocale != null) {
			// preResolveLocale을 기준으로 resolveLocale을 구한다.
			return getResolveLocale(preResolveLocale, usePreLocaleContextResolveInfoCondition.isCheckLanguageMatchOnly());
		}
		return null;
	}
}
