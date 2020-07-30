package net.luversof.boot.autoconfigure.core.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import net.luversof.boot.util.ApplicationContextUtil;


@Configuration(value = "_blueskyCoreAutoConfiguration", proxyBeanMethods = false)
@EnableConfigurationProperties(CoreProperties.class)
public class CoreAutoConfiguration {

	public CoreAutoConfiguration(ApplicationContext applicationContext) {
		ApplicationContextUtil.setApplicationContext(applicationContext);
	}

}
