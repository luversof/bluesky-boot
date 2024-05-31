package io.github.luversof.boot.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.PropertyMapper;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.BlueskyModuleProperties;
import lombok.Data;

@Data
public class CookieModuleProperties implements BlueskyModuleProperties<CookieProperties> {

	private final CookieProperties parent;
	
	private Map<String, CookieProperties> modules = new HashMap<>();

	@Override
	public void load() {
		var blueskyBootContext = BlueskyBootContextHolder.getContext();
		var moduleNameSet = blueskyBootContext.getModuleNameSet();
		var moduleInfoMap = blueskyBootContext.getModuleInfoMap();
		
		var propertyMapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
		
		moduleNameSet.forEach(moduleName -> {
			var builder = moduleInfoMap.containsKey(moduleName) ? moduleInfoMap.get(moduleName).getCookiePropertiesBuilder() : CookieProperties.builder();
			
			if (!getModules().containsKey(moduleName)) {
				getModules().put(moduleName, builder.build());
			}
			
			var cookieProperties = getModules().get(moduleName);
			
			propertyMapper.from(getParent()::isEnabled).to(builder::enabled);
			propertyMapper.from(cookieProperties::isEnabled).to(builder::enabled);
			propertyMapper.from(getParent()::getName).to(builder::name);
			propertyMapper.from(cookieProperties::getName).to(builder::name);
			propertyMapper.from(getParent()::getMaxAge).to(builder::maxAge);
			propertyMapper.from(cookieProperties::getMaxAge).to(builder::maxAge);
			propertyMapper.from(getParent()::getDomain).to(builder::domain);
			propertyMapper.from(cookieProperties::getDomain).to(builder::domain);
			propertyMapper.from(getParent()::getPath).to(builder::path);
			propertyMapper.from(cookieProperties::getPath).to(builder::path);
			propertyMapper.from(getParent()::isSecure).to(builder::secure);
			propertyMapper.from(cookieProperties::isSecure).to(builder::secure);
			propertyMapper.from(getParent()::isHttpOnly).to(builder::httpOnly);
			propertyMapper.from(cookieProperties::isHttpOnly).to(builder::httpOnly);
			propertyMapper.from(getParent()::getSameSite).to(builder::sameSite);
			propertyMapper.from(cookieProperties::getSameSite).to(builder::sameSite);
			
			getModules().put(moduleName, builder.build());
			
		});
	}
}
