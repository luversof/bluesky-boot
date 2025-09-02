package io.github.luversof.boot.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.BlueskyModuleProperties;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = WebProperties.PREFIX)
public class WebModuleProperties implements BlueskyModuleProperties<WebProperties> {
	
	private static final long serialVersionUID = 1L;

	@Autowired
	private WebProperties parent;
	
	private Map<String, WebProperties> modules = new HashMap<>();
	
	@Override
	public void load() {
		parentReload();
		var blueskyBootContext = BlueskyBootContextHolder.getContext();
		var moduleNameSet = blueskyBootContext.getModuleNameSet();
		var moduleInfoMap = blueskyBootContext.getModuleInfoMap();
		
		moduleNameSet.forEach(moduleName -> {
			
			var builder = moduleInfoMap.containsKey(moduleName) ? moduleInfoMap.get(moduleName).getWebPropertiesBuilder() : WebProperties.builder();
			
			if (!getModules().containsKey(moduleName)) {
				getModules().put(moduleName, builder.build());
			}
			
			var webProperties = getModules().get(moduleName);
			
			var propertyMapperConsumer = getParent().getPropertyMapperConsumer();
			propertyMapperConsumer.accept(getParent(), builder);
			propertyMapperConsumer.accept(getGroup(moduleName), builder);
			propertyMapperConsumer.accept(webProperties, builder);
			
			getModules().put(moduleName, builder.build());
		});
	}
}
