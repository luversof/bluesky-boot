package io.github.luversof.boot.autoconfigure.context.i18n;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.context.i18n.LocaleModuleProperties;
import io.github.luversof.boot.context.i18n.LocaleProperties;

@AutoConfiguration(value = "blueskyBootLocaleAutoConfiguration")
public class LocaleAutoConfiguration {
	
	@Bean(LocaleProperties.DEFAULT_BEAN_NAME)
	@Primary
	@ConfigurationProperties(prefix = "bluesky-boot.locale")
	LocaleProperties localeProperties() {
		return new LocaleProperties();
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
	
	@Bean
	@ConfigurationProperties(prefix = "bluesky-boot.other-locale")
	LocaleProperties otherLocaleProperties() {
		return new LocaleProperties();
	}
	
	@Bean
	@ConfigurationProperties(prefix = "bluesky-boot.other-locale")
	LocaleModuleProperties otherLocaleModuleProperties(@Qualifier("otherLocaleProperties") LocaleProperties otherLocaleProperties) {
		return new LocaleModuleProperties(
				otherLocaleProperties,
				moduleName -> {
					var moduleInfoMap = BlueskyBootContextHolder.getContext().getModuleInfoMap();
					return moduleInfoMap.containsKey(moduleName) ? moduleInfoMap.get(moduleName).getLocalePropertiesBuilder() : LocaleProperties.builder();
				}
		);
	}

}
