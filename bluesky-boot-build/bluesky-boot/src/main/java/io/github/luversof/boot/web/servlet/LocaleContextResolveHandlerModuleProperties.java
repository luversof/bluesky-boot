package io.github.luversof.boot.web.servlet;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.BlueskyModuleProperties;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = "bluesky-boot.web.servlet.locale-context-resolve-handler")
public class LocaleContextResolveHandlerModuleProperties implements BlueskyModuleProperties<LocaleContextResolveHandlerProperties> {

	private final LocaleContextResolveHandlerProperties parent;
	
	private Map<String, LocaleContextResolveHandlerProperties> modules = new HashMap<>();
	
	@Override
	public void load() {
		var blueskyBootContext = BlueskyBootContextHolder.getContext();
		var moduleNameSet = blueskyBootContext.getModuleNameSet();
		var moduleInfoMap = blueskyBootContext.getModuleInfoMap();
		
		var propertyMapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
		
		moduleNameSet.forEach(moduleName -> {
			var builder = LocaleContextResolveHandlerProperties.builder();
			
			if (!getModules().containsKey(moduleName)) {
				getModules().put(moduleName, builder.build());
			}
			
			var localeContextResolveHandlerProperties = getModules().get(moduleName);
			
			propertyMapper.from(getParent()::getResolveLocaleCookieCreate).to(builder::resolveLocaleCookieCreate);
			propertyMapper.from(localeContextResolveHandlerProperties::getResolveLocaleCookieCreate).to(builder::resolveLocaleCookieCreate);
			
			
			getModules().put(moduleName, builder.build());
			
		});
	}
}
