package io.github.luversof.boot.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.BlueskyModuleProperties;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = "bluesky-boot.web.locale-resolve-handler")
public class LocaleResolveHandlerModuleProperties implements BlueskyModuleProperties<LocaleResolveHandlerProperties>, BeanNameAware {
	
	private static final long serialVersionUID = 1L;

	private String beanName;

	private LocaleResolveHandlerProperties parent;
	
	private Map<String, LocaleResolveHandlerProperties> modules = new HashMap<>();
	
	public LocaleResolveHandlerModuleProperties(LocaleResolveHandlerProperties parent) {
		this.parent = parent;
	}
	
	@Override
	public void load() {
		this.parent = getParentByBeanName();
		var brickBootContext = BlueskyBootContextHolder.getContext();
		var moduleNameSet = brickBootContext.getModuleNameSet();
		
		var propertyMapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
		
		moduleNameSet.forEach(moduleName -> {
			var builder = LocaleResolveHandlerProperties.builder();
			
			if (!getModules().containsKey(moduleName)) {
				getModules().put(moduleName, builder.build());
			}
			
			var localeResolveHandlerProperties = getModules().get(moduleName);
			
			propertyMapper.from(getParent()::getLocaleResolveInfoCondition).to(builder::localeResolveInfoCondition);
			propertyMapper.from(localeResolveHandlerProperties::getLocaleResolveInfoCondition).to(builder::localeResolveInfoCondition);
			propertyMapper.from(getParent()::getSetRepresentativeCondition).to(builder::setRepresentativeCondition);
			propertyMapper.from(localeResolveHandlerProperties::getSetRepresentativeCondition).to(builder::setRepresentativeCondition);
			propertyMapper.from(getParent()::getPreLocaleResolveInfoCondition).to(builder::preLocaleResolveInfoCondition);
			propertyMapper.from(localeResolveHandlerProperties::getPreLocaleResolveInfoCondition).to(builder::preLocaleResolveInfoCondition);
			
			getModules().put(moduleName, builder.build());
		});
		
	}
}
