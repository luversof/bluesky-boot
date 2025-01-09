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
	
	private static final long serialVersionUID = 1L;

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
			propertyMapper.from(getParent()::getNotSupportedBrowserRegPatternList).to(builder::notSupportedBrowserRegPatternList);
			propertyMapper.from(webProperties::getNotSupportedBrowserRegPatternList).to(builder::notSupportedBrowserRegPatternList);
			propertyMapper.from(getParent()::getNotSupportedBrowserExcludePathPatternList).to(builder::notSupportedBrowserExcludePathPatternList);
			propertyMapper.from(webProperties::getNotSupportedBrowserExcludePathPatternList).to(builder::notSupportedBrowserExcludePathPatternList);
			
			getModules().put(moduleName, builder.build());
		});
	}
}
