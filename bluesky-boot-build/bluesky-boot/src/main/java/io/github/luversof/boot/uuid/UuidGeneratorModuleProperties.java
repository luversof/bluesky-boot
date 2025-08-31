package io.github.luversof.boot.uuid;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.BlueskyModuleProperties;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = UuidGeneratorProperties.PREFIX)
public class UuidGeneratorModuleProperties implements BlueskyModuleProperties<UuidGeneratorProperties> {

	private static final long serialVersionUID = 1L;
	
	private UuidGeneratorProperties parent;
	
	private Map<String, UuidGeneratorProperties> modules = new HashMap<>();

	@Override
	public void load() {
		parentReload();
		var blueskyBootContext = BlueskyBootContextHolder.getContext();
		var moduleNameSet = blueskyBootContext.getModuleNameSet();
		var moduleInfoMap = blueskyBootContext.getModuleInfoMap();
		
		moduleNameSet.forEach(moduleName -> {
			
			var builder = moduleInfoMap.containsKey(moduleName) ? moduleInfoMap.get(moduleName).getUuidGeneratorPropertiesBuilder() : UuidGeneratorProperties.builder();
			
			if (!getModules().containsKey(moduleName)) {
				getModules().put(moduleName, builder.build());
			}
			
			var uuidGeneratorProperties = getModules().get(moduleName);
			
			var propertyMapperConsumer = getParent().getPropertyMapperConsumer();
			propertyMapperConsumer.accept(getParent(), builder);
			propertyMapperConsumer.accept(uuidGeneratorProperties, builder);
			
			getModules().put(moduleName, builder.build());
		});
	}

}
