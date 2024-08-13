package io.github.luversof.boot.web.servlet.i18n.handler;

import io.github.luversof.boot.web.servlet.i18n.LocaleContextResolveInfo;
import io.github.luversof.boot.web.servlet.i18n.LocaleContextResolveInfoContainer;
import io.github.luversof.boot.context.BlueskyContextHolder;
import io.github.luversof.boot.context.i18n.LocaleProperties;
import io.github.luversof.boot.web.LocaleContextResolveHandlerProperties;
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
	 * localeContextResolveInfoContainer에 localeContextResolveInfo를 추가
	 * 대표 로케일로 지정할 대상인 경우 추가
	 * 대표 로케일로 지정할 조건이 있는 경우는 어떻게? 이 부분을 고민해보아야 함
	 * ex) 기존 대표 로케일이 있으면 덮어쓰지 않기 같은 옵션?
	 * @param localeContextResolveInfoContainer
	 * @param localeContextResolveInfo
	 */
	protected void addLocaleContextResolveInfo(LocaleContextResolveInfoContainer localeContextResolveInfoContainer, LocaleContextResolveInfo localeContextResolveInfo) {
		localeContextResolveInfoContainer.getResolveList().add(localeContextResolveInfo);
		if (Boolean.TRUE.equals(getLocaleContextResolveHandlerProperties().getSetRepresentative())) {
			localeContextResolveInfoContainer.setRepresentativeSupplier(() -> localeContextResolveInfo);
		}
	}
	
	protected LocaleProperties getLocaleProperties() {
		return BlueskyContextHolder.getProperties(LocaleProperties.class, localePropertiesBeanName);
	}
	
	protected LocaleContextResolveHandlerProperties getLocaleContextResolveHandlerProperties() {
		return BlueskyContextHolder.getProperties(LocaleContextResolveHandlerProperties.class, localeContextResolveHandlerPropertiesBeanName);
	}
}
