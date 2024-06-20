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
@ConfigurationProperties(prefix = "bluesky-boot.web")
public class WebModuleProperties implements BlueskyModuleProperties<WebProperties> {

	@Setter(onMethod_ = @Autowired)
	private WebProperties parent;
	
	private Map<String, WebProperties> modules = new HashMap<>();
	
	@Override
	public void load() {
		var blueskyBootContext = BlueskyBootContextHolder.getContext();
		var moduleNameSet = blueskyBootContext.getModuleNameSet();
		var moduleInfoMap = blueskyBootContext.getModuleInfoMap();
	
		var propertyMapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
		
		moduleNameSet.forEach(moduleName -> {
			
			var builder = moduleInfoMap.containsKey(moduleName) ? moduleInfoMap.get(moduleName).getWebPropertiesBuilder() : WebProperties.builder();
			
			if (!getModules().containsKey(moduleName)) {
				getModules().put(moduleName, builder.build());
			}
			
			var webProperties = getModules().get(moduleName);
			
			propertyMapper.from(getParent()::getCheckNotSupportedBrowser).to(builder::checkNotSupportedBrowser);
			propertyMapper.from(webProperties::getCheckNotSupportedBrowser).to(builder::checkNotSupportedBrowser);
			propertyMapper.from(getParent()::getNotSupportedBrowserRegPattern).to(builder::notSupportedBrowserRegPattern);
			propertyMapper.from(webProperties::getNotSupportedBrowserRegPattern).to(builder::notSupportedBrowserRegPattern);
			propertyMapper.from(getParent()::getNotSupportedBrowserExcludePathPatterns).to(builder::notSupportedBrowserExcludePathPatterns);
			propertyMapper.from(webProperties::getNotSupportedBrowserExcludePathPatterns).to(builder::notSupportedBrowserExcludePathPatterns);
			
			getModules().put(moduleName, builder.build());
		});
	}
}
