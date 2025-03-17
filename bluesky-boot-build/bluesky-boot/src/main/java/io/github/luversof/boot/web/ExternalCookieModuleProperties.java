package io.github.luversof.boot.web;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.util.function.SerializableFunction;
import io.github.luversof.boot.web.CookieProperties.CookiePropertiesBuilder;

@ConfigurationProperties(prefix = "brick-boot.web.external-cookie")
public class ExternalCookieModuleProperties extends CookieModuleProperties {

	private static final long serialVersionUID = 1L;

	@Override
	protected SerializableFunction<String, CookiePropertiesBuilder> getBuilderFunction() {
		return moduleName -> {
			var moduleInfoMap = BlueskyBootContextHolder.getContext().getModuleInfoMap();
			return moduleInfoMap.containsKey(moduleName) ? moduleInfoMap.get(moduleName).getExternalCookiePropertiesBuilder() : CookieProperties.builder();
		};
	}
	
	

}
