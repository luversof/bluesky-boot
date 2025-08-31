package io.github.luversof.boot.uuid;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.BlueskyGroupProperties;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = UuidGeneratorProperties.PREFIX)
public class UuidGeneratorGroupProperties implements BlueskyGroupProperties<UuidGeneratorProperties> {

	private static final long serialVersionUID = 1L;
	
	private UuidGeneratorProperties parent;
	
	private Map<String, UuidGeneratorProperties> groups = new HashMap<>();
	
	@Override
	public void load() {
		parentReload();
		var groupModuleInfoMap = BlueskyBootContextHolder.getContext().getGroupModuleInfoMap();
		
		BlueskyBootContextHolder.getContext().getGroupModules().keySet().forEach(groupName -> {
			var builder = groupModuleInfoMap.containsKey(groupName) ? groupModuleInfoMap.get(groupName).getUuidGeneratorPropertiesBuilder() : UuidGeneratorProperties.builder();
			
			// group이 없는 경우 기본 설정 추가
			if (!getGroups().containsKey(groupName)) {
				getGroups().put(groupName, builder.build());
			}
			
			var uuidGeneratorProperties = getGroups().get(groupName);
			var propertyMapperConsumer = getParent().getPropertyMapperConsumer();
			propertyMapperConsumer.accept(getParent(), builder);
			propertyMapperConsumer.accept(uuidGeneratorProperties, builder);
			
			getGroups().put(groupName, builder.build());
		});
	}

}
