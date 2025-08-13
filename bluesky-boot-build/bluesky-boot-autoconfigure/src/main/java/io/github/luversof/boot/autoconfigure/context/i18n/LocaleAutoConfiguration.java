package io.github.luversof.boot.autoconfigure.context.i18n;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import io.github.luversof.boot.context.i18n.ExternalLocaleGroupProperties;
import io.github.luversof.boot.context.i18n.ExternalLocaleModuleProperties;
import io.github.luversof.boot.context.i18n.ExternalLocaleProperties;
import io.github.luversof.boot.context.i18n.LocaleGroupProperties;
import io.github.luversof.boot.context.i18n.LocaleModuleProperties;
import io.github.luversof.boot.context.i18n.LocaleProperties;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Locale support.
 * 
 * @author bluesky
 */
@AutoConfiguration(value = "blueskyBootLocaleAutoConfiguration")
@EnableConfigurationProperties({
	LocaleProperties.class,
	LocaleModuleProperties.class,
	LocaleGroupProperties.class,
	ExternalLocaleProperties.class,
	ExternalLocaleModuleProperties.class,
	ExternalLocaleGroupProperties.class
})
public class LocaleAutoConfiguration {
	

}
