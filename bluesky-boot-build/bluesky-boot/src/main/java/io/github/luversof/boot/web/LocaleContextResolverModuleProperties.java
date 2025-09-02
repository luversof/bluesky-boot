package io.github.luversof.boot.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.BlueskyModuleProperties;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = LocaleContextResolverProperties.PREFIX)
public class LocaleContextResolverModuleProperties implements BlueskyModuleProperties<LocaleContextResolverProperties> {
	
	private static final long serialVersionUID = 1L;

	@Autowired
	private LocaleContextResolverProperties parent;
	
	private Map<String, LocaleContextResolverProperties> modules = new HashMap<>();
	
	@Override
	public void load() {
		parentReload();
		var blueskyBootContext = BlueskyBootContextHolder.getContext();
		var moduleNameSet = blueskyBootContext.getModuleNameSet();
		var moduleInfoMap = blueskyBootContext.getModuleInfoMap();
		
		moduleNameSet.forEach(moduleName -> {
			var builder = moduleInfoMap.containsKey(moduleName) ? moduleInfoMap.get(moduleName).getLocaleContextResolverPropertiesBuilder() : LocaleContextResolverProperties.builder();
			
			if (!getModules().containsKey(moduleName)) {
				getModules().put(moduleName, builder.build());
			}
			
			var localeContextResolverProperties = getModules().get(moduleName);
			
			var propertyMapperConsumer = getParent().getPropertyMapperConsumer();
			propertyMapperConsumer.accept(getParent(), builder);
			propertyMapperConsumer.accept(getGroup(moduleName), builder);
			propertyMapperConsumer.accept(localeContextResolverProperties, builder);
			
			getModules().put(moduleName, builder.build());
			
		});

		getModules().forEach((key, value) -> value.load());
	}

}
