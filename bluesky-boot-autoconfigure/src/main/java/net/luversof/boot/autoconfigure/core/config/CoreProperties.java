package net.luversof.boot.autoconfigure.core.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "bluesky-modules.core")
public class CoreProperties implements BlueskyProperties<net.luversof.boot.autoconfigure.core.config.CoreProperties.CoreModuleProperties> {

	
	private Map<String, CoreModuleProperties> modules;
	
	@Data
	public static class CoreModuleProperties {
	}
}
