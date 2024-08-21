package io.github.luversof.boot.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.BlueskyModuleProperties;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = "bluesky-boot.web.locale-context-resolve-handler")
public class LocaleContextResolveHandlerModuleProperties implements BlueskyModuleProperties<LocaleContextResolveHandlerProperties> {

private final LocaleContextResolveHandlerProperties parent;
	
	private Map<String, LocaleContextResolveHandlerProperties> modules = new HashMap<>();
	
	@Override
	public void load() {
		var brickBootContext = BlueskyBootContextHolder.getContext();
		var moduleNameSet = brickBootContext.getModuleNameSet();
		
		var propertyMapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
		
		moduleNameSet.forEach(moduleName -> {
			var builder = LocaleContextResolveHandlerProperties.builder();
			
			if (!getModules().containsKey(moduleName)) {
				getModules().put(moduleName, builder.build());
			}
			
			var localeContextResolveHandlerProperties = getModules().get(moduleName);
			
			propertyMapper.from(getParent()::getResolveLocaleContextCookieCreate).to(builder::resolveLocaleContextCookieCreate);
			propertyMapper.from(localeContextResolveHandlerProperties::getResolveLocaleContextCookieCreate).to(builder::resolveLocaleContextCookieCreate);
			propertyMapper.from(getParent()::getSetLocaleContextCookieCreate).to(builder::setLocaleContextCookieCreate);
			propertyMapper.from(localeContextResolveHandlerProperties::getSetLocaleContextCookieCreate).to(builder::setLocaleContextCookieCreate);
			propertyMapper.from(getParent()::getSetRepresentativeCondition).to(builder::setRepresentativeCondition);
			propertyMapper.from(localeContextResolveHandlerProperties::getSetRepresentativeCondition).to(builder::setRepresentativeCondition);
			propertyMapper.from(getParent()::getUsePreLocaleContextResolveInfoCondition).to(builder::usePreLocaleContextResolveInfoCondition);
			propertyMapper.from(localeContextResolveHandlerProperties::getUsePreLocaleContextResolveInfoCondition).to(builder::usePreLocaleContextResolveInfoCondition);
			
			getModules().put(moduleName, builder.build());
		});
		
	}
}
