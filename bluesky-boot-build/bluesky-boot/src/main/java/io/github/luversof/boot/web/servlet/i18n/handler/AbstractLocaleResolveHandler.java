package io.github.luversof.boot.web.servlet.i18n.handler;

import io.github.luversof.boot.web.servlet.i18n.LocaleResolveInfo;
import io.github.luversof.boot.web.servlet.i18n.LocaleResolveInfoContainer;

import java.util.Locale;

import org.springframework.util.CollectionUtils;

import io.github.luversof.boot.context.BlueskyContextHolder;
import io.github.luversof.boot.context.i18n.LocaleProperties;
import io.github.luversof.boot.web.LocaleResolveHandlerProperties;
import io.github.luversof.boot.web.LocaleResolveHandlerProperties.SetRepresentativeCondition;
import io.github.luversof.boot.web.LocaleResolveHandlerProperties.PreLocaleResolveInfoCondition;
import io.github.luversof.boot.web.servlet.i18n.LocaleResolveHandler;
import lombok.Getter;

/**
 * Abstract classes of CookieLocaleResolveHandler and AcceptHeaderLocaleResolveHandler
 */
public abstract class AbstractLocaleResolveHandler implements LocaleResolveHandler {
	
	/**
	 * localePropertiesBeanName to use
	 */
	@Getter
	private final String localePropertiesBeanName;
	
	/**
	 * localeResolveHandlerPropertiesBeanName to use
	 */
	@Getter
	private final String localeResolveHandlerPropertiesBeanName;

	/**
	 * Registered localeReolverHandler bean Name
	 */
	@Getter
	private String handlerBeanName;
	
	AbstractLocaleResolveHandler(String localePropertiesBeanName, String localeResolveHandlerPropertiesBeanName) {
		this.localePropertiesBeanName = localePropertiesBeanName;
		this.localeResolveHandlerPropertiesBeanName = localeResolveHandlerPropertiesBeanName;
	}
	
	@Override
	public void setBeanName(String name) {
		this.handlerBeanName = name;
	}
	
	/**
	 * Search for LocaleProperties based on Context
	 * In the case of LocaleProperties, multiple beans can be registered and used, so search based on localePropertiesBeanName
	 * 
	 * @return LocaleProperties
	 */
	protected LocaleProperties getLocaleProperties() {
		return BlueskyContextHolder.getProperties(LocaleProperties.class, localePropertiesBeanName);
	}
	
	/**
	 * Search for LocaleResolveHandlerProperties based on Context
	 * In the case of LocaleResolveHandlerProperties, multiple beans can be registered and used, so search based on localeResolveHandlerPropertiesBeanName
	 * 
	 * @return LocaleResolveHandlerProperties
	 */
	protected LocaleResolveHandlerProperties getLocaleResolveHandlerProperties() {
		return BlueskyContextHolder.getProperties(LocaleResolveHandlerProperties.class, localeResolveHandlerPropertiesBeanName);
	}
	
	/**
	 * Create a LocaleResolveInfo to be used in the corresponding handler.
	 * 
	 * @return The generated LocaleResolveInfo object
	 */
	protected LocaleResolveInfo createLocaleResolveInfo() {
		var localeResolveInfo = new LocaleResolveInfo();
		localeResolveInfo.setHandlerBeanName(getHandlerBeanName());
		return localeResolveInfo;
	}
	
	/**
	 * set localeContext
	 * 
	 * @param localeResolveInfo An object containing information related to locale resolve processing.
	 * @param localeResolveInfoContainer Passed in case of referencing the preceding localeResolveInfo
	 */
	protected void setResolveLocale(LocaleResolveInfo localeResolveInfo, LocaleResolveInfoContainer localeResolveInfoContainer) {
		var localeProperties = getLocaleProperties();
		if (localeProperties.getEnableLocaleList().isEmpty()) {
			return;
		}
		
		// locale이 단일이면 해당 로케일로 지정 처리
		if (localeProperties.getEnableLocaleList().size() == 1) {
			localeResolveInfo.setResolveLocale(localeProperties.getDefaultLocale());
			return;
		}
		
		var localeResolveHandlerProperties = getLocaleResolveHandlerProperties();
		var preLocaleResolveInfoCondition = localeResolveHandlerProperties.getPreLocaleResolveInfoCondition();
		
		Locale resolveLocale = null;
		// 선행 handler resolveLocale을 먼저 참고하는 경우
		if (PreLocaleResolveInfoCondition.USE_FIRST.equals(preLocaleResolveInfoCondition) || PreLocaleResolveInfoCondition.USE_LANGUAGE_FIRST.equals(preLocaleResolveInfoCondition)) {
			resolveLocale = getResolveLocaleByPreResolveInfo(localeResolveInfoContainer);
		}
		
		if (resolveLocale != null) {
			localeResolveInfo.setResolveLocale(resolveLocale);
			return;
		}
		
		// 내 자신 로케일 계산하고 있으면 해당 설정
		var requestLocale = localeResolveInfo.getRequestLocale();
		resolveLocale = getResolveLocale(requestLocale, false);
		if (resolveLocale != null) {
			localeResolveInfo.setResolveLocale(resolveLocale);
			return;
		}
		
		// 자신의 resolveLocale이 없으면 선행 로케일 참조하는지 체크 
		if (PreLocaleResolveInfoCondition.USE_WHEN_NOT_RESOLVED.equals(preLocaleResolveInfoCondition) || PreLocaleResolveInfoCondition.USE_LANGUAGE_WHEN_NOT_RESOLVED.equals(preLocaleResolveInfoCondition)) {
			resolveLocale = getResolveLocaleByPreResolveInfo(localeResolveInfoContainer);
		}
		
		if (resolveLocale != null) {
			localeResolveInfo.setResolveLocale(resolveLocale);
		}
		// 최종적으로 resolveLocale이 없는 경우 굳이 설정하지 않음
	}
	
	
	/**
	 * Add localeResolveInfo to localeResolveInfoContainer
	 * 
	 * @param localeResolveInfoContainer Container containing locale information to use
	 * @param localeResolveInfo An object containing information related to locale resolve processing.
	 */
	protected void addLocaleResolveInfo(LocaleResolveInfoContainer localeResolveInfoContainer, LocaleResolveInfo localeResolveInfo) {
		localeResolveInfoContainer.getResolveList().add(localeResolveInfo);
	}
	
	/**
	 * Set the representative locale
	 * 
	 * @param localeResolveInfoContainer Container containing locale information to use
	 * @param localeResolveInfo An object containing information related to locale resolve processing.
	 */
	protected void setRepresenatativeSupplier(LocaleResolveInfoContainer localeResolveInfoContainer, LocaleResolveInfo localeResolveInfo) {
		var localeResolveHandlerProperties = getLocaleResolveHandlerProperties();
		var setRepresentativeCondition = localeResolveHandlerProperties.getSetRepresentativeCondition();
		
		switch(setRepresentativeCondition == null ? SetRepresentativeCondition.NONE : setRepresentativeCondition) {
		case NONE:
			break;
		case OVERWRITE:
			localeResolveInfoContainer.setRepresentativeSupplier(() -> localeResolveInfo);
			break;
		case OVERWRITE_IF_NOT_EXISTS:
			if (localeResolveInfoContainer.getRepresentativeSupplier() == null || localeResolveInfoContainer.getRepresentativeSupplier().get().getResolveLocale() == null) {
				localeResolveInfoContainer.setRepresentativeSupplier(() -> localeResolveInfo);
			}
			break;
		default:
			break;
		}
	}

	/**
	 * Obtain the currently set locale based on the target Locale
	 * 
	 * @param targetLocale targetLocale
	 * @param checkLanguageMatchOnly checkLanguageMatchOnly
	 * @return resolved locale
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
	 * Calculate resolveLocale based on the locale calculated in the preceding handler
	 * 
	 * @param localeResolveInfoContainer Container containing locale information to use
	 * @return resolved locale
	 */
	protected Locale getResolveLocaleByPreResolveInfo(LocaleResolveInfoContainer localeResolveInfoContainer) {
		var resolveList = localeResolveInfoContainer.getResolveList();
		var preResolveLocale = CollectionUtils.isEmpty(resolveList) ? null : resolveList.getLast().getResolveLocale();
		
		var localeResolveHandlerProperties = getLocaleResolveHandlerProperties();
		var preLocaleResolveInfoCondition = localeResolveHandlerProperties.getPreLocaleResolveInfoCondition();
		
		if (preResolveLocale != null) {
			// preResolveLocale을 기준으로 resolveLocale을 구한다.
			return getResolveLocale(preResolveLocale, preLocaleResolveInfoCondition.isCheckLanguageMatchOnly());
		}
		return null;
	}
	

	
}
