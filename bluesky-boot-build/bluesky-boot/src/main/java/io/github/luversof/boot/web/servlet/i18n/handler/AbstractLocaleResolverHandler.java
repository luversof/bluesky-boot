package io.github.luversof.boot.web.servlet.i18n.handler;

import io.github.luversof.boot.web.servlet.i18n.LocaleResolveInfoContainer;
import io.github.luversof.boot.web.servlet.i18n.LocaleResolveInfo;
import io.github.luversof.boot.web.servlet.i18n.LocaleResolveHandler;
import lombok.Getter;

public abstract class AbstractLocaleResolverHandler implements LocaleResolveHandler {
	
	/**
	 *  실행할 순서
	 */
	@Getter
	private final int order;
	
	@Getter
	private final String localePropertiesBeanName;

	/**
	 * 등록된 localeReolverHandler bean Name
	 */
	@Getter
	private String handlerBeanName;
	
	AbstractLocaleResolverHandler(int order, String localePropertiesBeanName) {
		this.order = order;
		this.localePropertiesBeanName = localePropertiesBeanName;
	}
	
	@Override
	public void setBeanName(String name) {
		this.handlerBeanName = name;
	}
	
	
	/**
	 * 해당 handler에서 사용할 LocaleResolveInfo 생성
	 * @param localeResolveInfoContainer
	 * @return
	 */
	protected LocaleResolveInfo createLocaleResolveInfo(LocaleResolveInfoContainer localeResolveInfoContainer) {
		var localeResolveInfo = new LocaleResolveInfo();
		localeResolveInfo.setOrder(getOrder());
		localeResolveInfo.setHandlerBeanName(getHandlerBeanName());
		
		localeResolveInfoContainer.getLocaleResolveInfoList().add(localeResolveInfo);
		return localeResolveInfo;
	}
}
