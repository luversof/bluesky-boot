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
		
		// 설정된 group의 module을 기준으로 groupModuleInfoMap 생성
		// group에 moduleInfo 정보가 없으면 상위 moduleInfo(CoreProperties) 정보로 설정
		// Core의 moduleInfo를 blueskyBootContext에 담아 여러 GroupProperties 생성 시 참고하기 위함 
		groupModules.keySet().forEach(groupName -> {
			// group이 없는 경우 기본 설정 추가
			if (!getGroups().containsKey(groupName)) {
				getGroups().put(groupName, getParent().getModuleInfo() == null ? CoreProperties.builder().build() : getParent().getModuleInfo().getCorePropertiesBuilder().build());
			}
			
			// blueskyBootContext에 groupModuleInfo 정보 추가
			if (getGroups().get(groupName).getModuleInfo() != null) {
				groupModuleInfoMap.put(groupName, getGroups().get(groupName).getModuleInfo());
			}
		});
		
		groupModules.keySet().forEach(groupName -> {
			
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
