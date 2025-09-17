package io.github.luversof.boot.core;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = CoreProperties.PREFIX)
public class CoreModuleProperties implements BlueskyModuleProperties<CoreProperties> {
	
	private static final long serialVersionUID = 1L;
	
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
		
		moduleNameSet.forEach(moduleName -> {
			// blueskyBootContext moduleInfoMap에 추가
			if (getModules().get(moduleName).getModuleInfo() != null) {
				moduleInfoMap.put(moduleName, getModules().get(moduleName).getModuleInfo());
			}
			
			var builder = moduleInfoMap.containsKey(moduleName) ? moduleInfoMap.get(moduleName).getCorePropertiesBuilder() : CoreProperties.builder();
			
			if (!getModules().containsKey(moduleName)) {
				getModules().put(moduleName, builder.build());
			}
			
			var propertyMapperConsumer = getParent().getPropertyMapperConsumer();
			propertyMapperConsumer.accept(getParent(), builder);
			propertyMapperConsumer.accept(getGroup(moduleName), builder);
			propertyMapperConsumer.accept(getModules().get(moduleName), builder);
			
			getModules().put(moduleName, builder.build());
		});

	}

}
