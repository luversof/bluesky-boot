package io.github.luversof.boot.core;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = "bluesky-boot.core")
public class CoreModuleProperties implements BlueskyModuleProperties<CoreProperties> {
	
	@Autowired
	private CoreProperties parent;
	
	private Map<String, CoreProperties> modules = new HashMap<>();
	
	@Override
	public void afterPropertiesSet() throws Exception {
		 
		if (getModules() == null) {
			return;
		}
		
		var propertyMapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
		
		for (String key : getModules().keySet()) {
			var coreModuleProperties = getModules().get(key);
			var builder = coreModuleProperties.getModuleInfo() == null ? CoreProperties.builder() : coreModuleProperties.getModuleInfo().getCoreBuilder();
			propertyMapper.from(getParent()::getModuleInfo).to(builder::moduleInfo);
			propertyMapper.from(coreModuleProperties::getModuleInfo).to(builder::moduleInfo);
			propertyMapper.from(getParent()::getAddPathPatterns).to(builder::addPathPatterns);
			propertyMapper.from(coreModuleProperties::getAddPathPatterns).to(builder::addPathPatterns);
			propertyMapper.from(getParent()::getCheckNotSupportedBrowser).to(builder::checkNotSupportedBrowser);
			propertyMapper.from(coreModuleProperties::getCheckNotSupportedBrowser).to(builder::checkNotSupportedBrowser);
			propertyMapper.from(getParent()::getNotSupportedBrowserRegPattern).to(builder::notSupportedBrowserRegPattern);
			propertyMapper.from(coreModuleProperties::getNotSupportedBrowserRegPattern).to(builder::notSupportedBrowserRegPattern);
			propertyMapper.from(getParent()::getNotSupportedBrowserExcludePathPatterns).to(builder::notSupportedBrowserExcludePathPatterns);
			propertyMapper.from(coreModuleProperties::getNotSupportedBrowserExcludePathPatterns).to(builder::notSupportedBrowserExcludePathPatterns);
			
			getModules().put(key, builder.build());
		}
		
		BlueskyBootContextHolder.getContext().getModuleNameList().addAll(this.getModules().keySet());

	}

}
