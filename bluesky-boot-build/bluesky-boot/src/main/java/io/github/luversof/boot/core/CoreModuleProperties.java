package io.github.luversof.boot.core;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "bluesky-boot.core")
public class CoreModuleProperties implements BlueskyModuleProperties<CoreProperties>, InitializingBean {
	
	private Map<String, CoreProperties> modules = new HashMap<>();
	
	@Override
	public void afterPropertiesSet() throws Exception {
		if (this.getModules().isEmpty()) {
			this.getModules().put("defaultModule", new CoreProperties());
		}
	}
}
