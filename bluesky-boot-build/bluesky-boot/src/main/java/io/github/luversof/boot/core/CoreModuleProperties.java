package io.github.luversof.boot.core;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = CoreModuleProperties.PREFIX)
public class CoreModuleProperties implements BlueskyModuleProperties<CoreProperties> {
	
	private static final long serialVersionUID = 1L;
	
	public static final String PREFIX = "bluesky-boot.core";
	
	/**
	 * Bean 생성 시 지정할 이름
	 */
	public static final String BEAN_NAME = "blueskyCoreModuleProperties";

	@Autowired
	private CoreProperties parent;
	
	private Map<String, CoreProperties> modules = new HashMap<>();
	
	@Override
	public void load() {
		parentReload();
		var blueskyBootContext = BlueskyBootContextHolder.getContext();
		var moduleNameSet = blueskyBootContext.getModuleNameSet();
		var moduleInfoMap = blueskyBootContext.getModuleInfoMap();
		
		// coreProperties의 경우 moduleNameSet과 modules의 key를 병합한다.
		moduleNameSet.addAll(getModules().keySet());
		
		moduleNameSet.forEach(moduleName -> {
			if (!getModules().containsKey(moduleName)) {
				getModules().put(moduleName, getParent().getModuleInfo() == null ? CoreProperties.builder().build() : getParent().getModuleInfo().getCorePropertiesBuilder().build());
			}
		});
		
		var propertyMapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
		moduleNameSet.forEach(moduleName -> {
			if (getModules().get(moduleName).getModuleInfo() != null) {
				moduleInfoMap.put(moduleName, getModules().get(moduleName).getModuleInfo());
			}
			
			var builder = moduleInfoMap.containsKey(moduleName) ? moduleInfoMap.get(moduleName).getCorePropertiesBuilder() : CoreProperties.builder();
			
			if (!getModules().containsKey(moduleName)) {
				getModules().put(moduleName, builder.build());
			}
			
			var coreProperties = getModules().get(moduleName);
			
			propertyMapper.from(getParent()::getModuleInfo).to(builder::moduleInfo);
			propertyMapper.from(coreProperties::getModuleInfo).to(builder::moduleInfo);
			
			var propertiesMap = new HashMap<String, String>();
			if (getParent().getProperties() != null) {
				propertiesMap.putAll(getParent().getProperties());
			}
			if (coreProperties.getProperties() != null) {
				propertiesMap.putAll(coreProperties.getProperties());
			}
			
			propertyMapper.from(propertiesMap).to(builder::properties);
			
			getModules().put(moduleName, builder.build());
		});

	}
	
	@Autowired
	void setCoreBaseProperties(CoreBaseProperties coreBaseProperties) {
		// DO NOTHING
	}

}
