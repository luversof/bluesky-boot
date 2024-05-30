package io.github.luversof.boot.autoconfigure.core;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.context.i18n.LocaleModuleProperties;
import io.github.luversof.boot.context.i18n.LocaleProperties;
import io.github.luversof.boot.core.CoreBaseProperties;
import io.github.luversof.boot.core.CoreModuleProperties;
import io.github.luversof.boot.core.CoreProperties;


/**
 * {@link EnableAutoConfiguration Auto-configuration} for Core support.
 * @author bluesky
 *
 */
@AutoConfiguration("blueskyBootCoreAutoConfiguration")
@EnableConfigurationProperties({ 
	CoreBaseProperties.class,
	CoreProperties.class,
	CoreModuleProperties.class,
})
public class CoreAutoConfiguration {
	
	@Bean
	@Primary
	@ConfigurationProperties(prefix = "bluesky-boot.locale")
	LocaleProperties localeProperties() {
		return new LocaleProperties();
	}
	
	@Bean
	@Primary
	@ConfigurationProperties(prefix = "bluesky-boot.locale")
	LocaleModuleProperties localeModuleProperties(LocaleProperties localeProperties) {
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
	LocaleModuleProperties otherLocaleModuleProperties(LocaleProperties otherLocaleProperties) {
		return new LocaleModuleProperties(
				otherLocaleProperties,
				moduleName -> {
					var moduleInfoMap = BlueskyBootContextHolder.getContext().getModuleInfoMap();
					return moduleInfoMap.containsKey(moduleName) ? moduleInfoMap.get(moduleName).getLocalePropertiesBuilder() : LocaleProperties.builder();
				}
		);
	}

}