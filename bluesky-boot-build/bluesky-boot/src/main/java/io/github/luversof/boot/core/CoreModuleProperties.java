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
	
	@Autowired
	private CoreBaseProperties base;
	
	private Map<String, CoreProperties> modules = new HashMap<>();
	
	@Override
	public void afterPropertiesSet() throws Exception {
		 
		if (getModules() == null) {
			return;
		}
		
		var blueskyBootContext = BlueskyBootContextHolder.getContext();
		blueskyBootContext.setParentModuleInfo(getParent().getModuleInfo());
		
		var propertyMapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
		
		blueskyBootContext.getModuleNameSet().forEach(moduleName -> {
			if (!getModules().containsKey(moduleName)) {
				getModules().put(moduleName, getParent().getModuleInfo() != null ? getParent().getModuleInfo().getCorePropertiesBuilder().build() : CoreProperties.builder().build());
			}
		});
		
		for (String key : getModules().keySet()) {
			var coreModuleProperties = getModules().get(key);
			
			CoreProperties.CorePropertiesBuilder builder = null;
			if (coreModuleProperties.getModuleInfo() != null) {
				builder = coreModuleProperties.getModuleInfo().getCorePropertiesBuilder();
			} else if (getParent().getModuleInfo() != null) {
				builder = getParent().getModuleInfo().getCorePropertiesBuilder();
			} else {
				builder = CoreProperties.builder();
			}
			
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
			
			// domain 할 차례
			
			getModules().put(key, builder.build());
			
			
			
			blueskyBootContext.getModuleNameSet().add(key);
			if (getModules().get(key).getModuleInfo() != null) {
				blueskyBootContext.getModuleInfoMap().put(key, getModules().get(key).getModuleInfo());
			}
		}

	}
	
	@Override
	public void setCoreModuleProperties(CoreModuleProperties coreModuleProperties) {
		
	}

}
