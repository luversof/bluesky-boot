package io.github.luversof.boot.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.BlueskyGroupProperties;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = WebProperties.PREFIX)
public class WebGroupProperties implements BlueskyGroupProperties<WebProperties> {

	private static final long serialVersionUID = 1L;
	
	@Autowired
	private WebProperties parent;
	
	private Map<String, WebProperties> groups = new HashMap<>();
	
	@Override
	public void load() {
		parentReload();
		var groupModuleInfoMap = BlueskyBootContextHolder.getContext().getGroupModuleInfoMap();
		
		BlueskyBootContextHolder.getContext().getGroupModules().keySet().forEach(groupName -> {
			var builder = groupModuleInfoMap.containsKey(groupName) ? groupModuleInfoMap.get(groupName).getWebPropertiesBuilder() : WebProperties.builder();
			
			// group이 없는 경우 기본 설정 추가
			if (!getGroups().containsKey(groupName)) {
				getGroups().put(groupName, builder.build());
			}
			
			var webProperties = getGroups().get(groupName);
			var propertyMapperConsumer = getParent().getPropertyMapperConsumer();
			propertyMapperConsumer.accept(getParent(), builder);
			propertyMapperConsumer.accept(webProperties, builder);
			
			getGroups().put(groupName, builder.build());
		});
	}

}
