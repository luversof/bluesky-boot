package io.github.luversof.boot.web.servlet.i18n.handler;

import io.github.luversof.boot.web.servlet.i18n.LocaleContextResolveInfoContainer;
import io.github.luversof.boot.web.servlet.i18n.LocaleContextResolveInfo;
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
	protected LocaleContextResolveInfo createLocaleResolveInfo(LocaleContextResolveInfoContainer localeContextResolveInfoContainer) {
		var localeResolveInfo = new LocaleContextResolveInfo();
		localeResolveInfo.setOrder(getOrder());
		localeResolveInfo.setHandlerBeanName(getHandlerBeanName());
		
		localeContextResolveInfoContainer.getResolveList().add(localeResolveInfo);
		return localeResolveInfo;
	}
}
