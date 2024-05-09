package io.github.luversof.boot.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.BlueskyModuleProperties;
import lombok.Data;
import lombok.Setter;

@Data
@ConfigurationProperties(prefix = "bluesky-boot.cookie")
public class CookieModuleProperties implements BlueskyModuleProperties<CookieProperties> {

	@Setter(onMethod_ = @Autowired)
	private CookieProperties parent;
	
	private Map<String, CookieProperties> modules = new HashMap<>();

	@Override
	public void afterPropertiesSet() throws Exception {
		
		var moduleInfoMap = BlueskyBootContextHolder.getContext().getModuleInfoMap();
		
		var propertyMapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
		
		BlueskyBootContextHolder.getContext().getModuleNameSet().forEach(moduleName -> {
			var builder = moduleInfoMap.containsKey(moduleName) ? moduleInfoMap.get(moduleName).getCookiePropertiesBuilder() : CookieProperties.builder();
			
			if (!getModules().containsKey(moduleName)) {
				getModules().put(moduleName, builder.build());
			}
			
			var cookieModuleProperties = getModules().get(moduleName);
			
			propertyMapper.from(getParent()::getName).to(builder::name);
			propertyMapper.from(cookieModuleProperties::getName).to(builder::name);
			propertyMapper.from(getParent()::getMaxAge).to(builder::maxAge);
			propertyMapper.from(cookieModuleProperties::getMaxAge).to(builder::maxAge);
			propertyMapper.from(getParent()::getDomain).to(builder::domain);
			propertyMapper.from(cookieModuleProperties::getDomain).to(builder::domain);
			propertyMapper.from(getParent()::getPath).to(builder::path);
			propertyMapper.from(cookieModuleProperties::getPath).to(builder::path);
			propertyMapper.from(getParent()::getSecure).to(builder::secure);
			propertyMapper.from(cookieModuleProperties::getSecure).to(builder::secure);
			propertyMapper.from(getParent()::getHttpOnly).to(builder::httpOnly);
			propertyMapper.from(cookieModuleProperties::getHttpOnly).to(builder::httpOnly);
			propertyMapper.from(getParent()::getSameSite).to(builder::sameSite);
			propertyMapper.from(cookieModuleProperties::getSameSite).to(builder::sameSite);
			
			getModules().put(moduleName, builder.build());
			
		});
	}
}
