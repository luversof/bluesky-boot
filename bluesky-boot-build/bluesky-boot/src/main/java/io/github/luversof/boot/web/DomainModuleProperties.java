package io.github.luversof.boot.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.BlueskyModuleProperties;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = DomainProperties.PREFIX)
public class DomainModuleProperties implements BlueskyModuleProperties<DomainProperties> {

	private static final long serialVersionUID = 1L;

	@Autowired
	private DomainProperties parent;
	
	private Map<String, DomainProperties> modules = new HashMap<>();

	@Override
	public void load() {
		parentReload();
		var blueskyBootContext = BlueskyBootContextHolder.getContext();
		var moduleNameSet = blueskyBootContext.getModuleNameSet();
		var moduleInfoMap = blueskyBootContext.getModuleInfoMap();
		
		moduleNameSet.forEach(moduleName -> {
			var builder = moduleInfoMap.containsKey(moduleName) ? moduleInfoMap.get(moduleName).getDomainPropertiesBuilder() : DomainProperties.builder();
			
			if (!getModules().containsKey(moduleName)) {
				getModules().put(moduleName, builder.build());
			}
			
			var domainProperties = getModules().get(moduleName);
			
			var propertyMapperConsumer = getParent().getPropertyMapperConsumer();
			propertyMapperConsumer.accept(getParent(), builder);
			propertyMapperConsumer.accept(getGroup(moduleName), builder);
			propertyMapperConsumer.accept(domainProperties, builder);
			
			getModules().put(moduleName, builder.build());
		});
	}

	
}
