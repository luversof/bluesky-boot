package net.luversof.boot.autoconfigure.core.config;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import net.luversof.boot.autoconfigure.core.util.ApplicationContextUtil;

@Configuration
public class CoreAutoConfiguration {

	public CoreAutoConfiguration(ApplicationContext applicationContext) {
		ApplicationContextUtil.setApplicationContext(applicationContext);
	}

}
