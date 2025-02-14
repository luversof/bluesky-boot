package io.github.luversof.boot.autoconfigure.context.i18n;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.context.i18n.LocaleModuleProperties;
import io.github.luversof.boot.context.i18n.LocaleProperties;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Locale support.
 * 
 * @author bluesky
 */
@AutoConfiguration(value = "blueskyBootLocaleAutoConfiguration")
public class LocaleAutoConfiguration {
	
	@Bean(LocaleProperties.DEFAULT_BEAN_NAME)
	@Primary
	@ConfigurationProperties(prefix = "bluesky-boot.locale")
	LocaleProperties localeProperties() {
		return new LocaleProperties(() -> {
			var parentModuleInfo = BlueskyBootContextHolder.getContext().getParentModuleInfo();
			return parentModuleInfo == null ? LocaleProperties.builder() : parentModuleInfo.getLocalePropertiesBuilder();
		});
	}
	
	@Bean(LocaleModuleProperties.DEFAULT_BEAN_NAME)
	@Primary
	@ConfigurationProperties(prefix = "bluesky-boot.locale")
	LocaleModuleProperties localeModuleProperties(@Qualifier(LocaleProperties.DEFAULT_BEAN_NAME) LocaleProperties localeProperties) {
		return new LocaleModuleProperties(
				localeProperties, 
				moduleName -> {
					var moduleInfoMap = BlueskyBootContextHolder.getContext().getModuleInfoMap();
					return moduleInfoMap.containsKey(moduleName) ? moduleInfoMap.get(moduleName).getLocalePropertiesBuilder() : LocaleProperties.builder();
				}
		);
	}
	
	@Bean(LocaleProperties.EXTERNAL_LOCALE_BEAN_NAME)
	@ConfigurationProperties(prefix = "bluesky-boot.external-locale")
	LocaleProperties externalLocaleProperties() {
		return new LocaleProperties(() -> {
			var parentModuleInfo = BlueskyBootContextHolder.getContext().getParentModuleInfo();
			return parentModuleInfo == null ? LocaleProperties.builder() : parentModuleInfo.getExternalLocalePropertiesBuilder();
		});
	}
	
	/**
	 * external-locale이랑 locale이랑 동일 builder를 사용하고 있음. (ServiceInfo 도 동일 정보 사용)
	 * 분기 처리 필요한지 검토 필요
	 * @param localeProperties
	 * @return
	 */
	@Bean(LocaleModuleProperties.EXTERNAL_LOCALE_BEAN_NAME)
	@ConfigurationProperties(prefix = "bluesky-boot.external-locale")
	LocaleModuleProperties externalLocaleModuleProperties(@Qualifier(LocaleProperties.EXTERNAL_LOCALE_BEAN_NAME) LocaleProperties localeProperties) {
		return new LocaleModuleProperties(
				localeProperties,
				moduleName -> {
					var moduleInfoMap = BlueskyBootContextHolder.getContext().getModuleInfoMap();
					return moduleInfoMap.containsKey(moduleName) ? moduleInfoMap.get(moduleName).getExternalLocalePropertiesBuilder() : LocaleProperties.builder();
				}
		);
	}

}
