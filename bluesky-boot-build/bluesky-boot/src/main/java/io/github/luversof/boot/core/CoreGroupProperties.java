package io.github.luversof.boot.core;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = CoreProperties.PREFIX)
public class CoreGroupProperties implements BlueskyGroupProperties<CoreProperties> {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * Bean 생성 시 지정할 이름
	 */
	public static final String BEAN_NAME = "blueskyCoreGroupProperties";

	@Autowired
	private CoreProperties parent;
	
	private Map<String, CoreProperties> groups = new HashMap<>();

	@Override
	public void load() {
		parentReload();
		var blueskyBootContext = BlueskyBootContextHolder.getContext();
		var groupModules = blueskyBootContext.getGroupModules();
		var groupModuleInfoMap = blueskyBootContext.getGroupModuleInfoMap();
		
		// group이 없는 경우 기본 설정 추가
		groupModules.keySet().forEach(groupName -> {
			if (!getGroups().containsKey(groupName)) {
				getGroups().put(groupName, getParent().getModuleInfo() == null ? CoreProperties.builder().build() : getParent().getModuleInfo().getCorePropertiesBuilder().build());
			}
		});
		
		groupModules.keySet().forEach(groupName -> {
			// blueskyBootContext에 groupModuleInfo 정보 추가
			if (getGroups().get(groupName).getModuleInfo() != null) {
				groupModuleInfoMap.put(groupName, getGroups().get(groupName).getModuleInfo());
			}
			
			var builder = groupModuleInfoMap.containsKey(groupName) ? groupModuleInfoMap.get(groupName).getCorePropertiesBuilder() : CoreProperties.builder();
			
			if (!getGroups().containsKey(groupName)) {
				getGroups().put(groupName, builder.build());
			}
			
			var propertyMapperConsumer = getParent().getPropertyMapperConsumer();
			propertyMapperConsumer.accept(getParent(), builder);
			propertyMapperConsumer.accept(getGroups().get(groupName), builder);
			
			getGroups().put(groupName, builder.build());
		});
		
	}
	
	
}
