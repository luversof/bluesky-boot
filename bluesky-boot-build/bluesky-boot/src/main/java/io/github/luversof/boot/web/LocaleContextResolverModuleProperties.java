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
@ConfigurationProperties(prefix = "bluesky-boot.web.locale-context-resolver")
public class LocaleContextResolverModuleProperties implements BlueskyModuleProperties<LocaleContextResolverProperties> {

	@Setter(onMethod_ = @Autowired)
	private LocaleContextResolverProperties parent;
	
	private Map<String, LocaleContextResolverProperties> modules = new HashMap<>();
	
	@Override
	public void load() {
		var blueskyBootContext = BlueskyBootContextHolder.getContext();
		var moduleNameSet = blueskyBootContext.getModuleNameSet();
		
		var propertyMapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
		
		moduleNameSet.forEach(moduleName -> {
			var builder = LocaleContextResolverProperties.builder();
			
			if (!getModules().containsKey(moduleName)) {
				getModules().put(moduleName, builder.build());
			}
			
			var localeContextResolverProperties = getModules().get(moduleName);
			
			propertyMapper.from(getParent()::getLocaleResolverHandlerBeanNameList).to(builder::localeResolverHandlerBeanNameList);
			propertyMapper.from(localeContextResolverProperties::getLocaleResolverHandlerBeanNameList).to(builder::localeResolverHandlerBeanNameList);
			
			getModules().put(moduleName, builder.build());
		});
	}

}
