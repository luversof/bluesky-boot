package io.github.luversof.boot.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.BlueskyGroupProperties;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = DomainProperties.PREFIX)
public class DomainGroupProperties implements BlueskyGroupProperties<DomainProperties> {

	private static final long serialVersionUID = 1L;
	
	@Autowired
	private DomainProperties parent;
	
	private Map<String, DomainProperties> groups = new HashMap<>();
	
	@Override
	public void load() {
		parentReload();
		var groupModuleInfoMap = BlueskyBootContextHolder.getContext().getGroupModuleInfoMap();
		
		BlueskyBootContextHolder.getContext().getGroupModules().keySet().forEach(groupName -> {
			var builder = groupModuleInfoMap.containsKey(groupName) ? groupModuleInfoMap.get(groupName).getDomainPropertiesBuilder() : DomainProperties.builder();
			
			// group이 없는 경우 기본 설정 추가
			if (!getGroups().containsKey(groupName)) {
				getGroups().put(groupName, builder.build());
			}
			
			var domainProperties = getGroups().get(groupName);
			var propertyMapperConsumer = getParent().getPropertyMapperConsumer();
			propertyMapperConsumer.accept(getParent(), builder);
			propertyMapperConsumer.accept(domainProperties, builder);
			
			getGroups().put(groupName, builder.build());
		});
	}
	
}
