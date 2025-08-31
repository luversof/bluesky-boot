package io.github.luversof.boot.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.BlueskyModuleProperties;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = LocaleResolveHandlerProperties.PREFIX)
public class LocaleResolveHandlerGroupProperties implements BlueskyModuleProperties<LocaleResolveHandlerProperties>, BeanNameAware {
	
	private static final long serialVersionUID = 1L;

	private String beanName;

	private LocaleResolveHandlerProperties parent;
	
	private Map<String, LocaleResolveHandlerProperties> modules = new HashMap<>();
	
	public LocaleResolveHandlerGroupProperties(LocaleResolveHandlerProperties parent) {
		this.parent = parent;
	}
	
	@Override
	public void load() {
		parentReload();
		var blueskyBootContext = BlueskyBootContextHolder.getContext();
		var moduleNameSet = blueskyBootContext.getModuleNameSet();
		var moduleInfoMap = blueskyBootContext.getModuleInfoMap();
		
		moduleNameSet.forEach(moduleName -> {
			var builder = moduleInfoMap.containsKey(moduleName) ? moduleInfoMap.get(moduleName).getLocaleResolveHandlerPropertiesBuilder() : LocaleResolveHandlerProperties.builder();
			
			if (!getModules().containsKey(moduleName)) {
				getModules().put(moduleName, builder.build());
			}
			
			var localeResolveHandlerProperties = getModules().get(moduleName);
			
			var propertyMapperConsumer = getParent().getPropertyMapperConsumer();
			
			propertyMapperConsumer.accept(getParent(), builder);
			propertyMapperConsumer.accept(localeResolveHandlerProperties, builder);
			
			getModules().put(moduleName, builder.build());
		});
		
	}
}
