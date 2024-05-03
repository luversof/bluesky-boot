package io.github.luversof.boot.autoconfigure.core;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;

import io.github.luversof.boot.context.i18n.LocaleModuleProperties;
import io.github.luversof.boot.context.i18n.LocaleProperties;
import io.github.luversof.boot.core.CoreBaseProperties;
import io.github.luversof.boot.core.CoreModuleProperties;
import io.github.luversof.boot.core.CoreProperties;
import io.github.luversof.boot.util.ApplicationContextUtil;
import io.github.luversof.boot.web.CookieModuleProperties;
import io.github.luversof.boot.web.CookieProperties;


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
	LocaleProperties.class,
	LocaleModuleProperties.class
})
public class CoreAutoConfiguration {

	CoreAutoConfiguration(ApplicationContext applicationContext) {
		ApplicationContextUtil.setApplicationContext(applicationContext);
	}

}
