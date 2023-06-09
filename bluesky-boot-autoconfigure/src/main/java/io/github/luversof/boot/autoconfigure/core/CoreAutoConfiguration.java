package io.github.luversof.boot.autoconfigure.core;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;

import io.github.luversof.boot.util.ApplicationContextUtil;


@AutoConfiguration("_blueskyBootCoreAutoConfiguration")
@EnableConfigurationProperties(CoreProperties.class)
public class CoreAutoConfiguration {

	public CoreAutoConfiguration(ApplicationContext applicationContext) {
		ApplicationContextUtil.setApplicationContext(applicationContext);
	}

}
