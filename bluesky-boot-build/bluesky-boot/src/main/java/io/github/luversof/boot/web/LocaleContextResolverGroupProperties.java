package io.github.luversof.boot.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.BlueskyGroupProperties;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = LocaleContextResolverProperties.PREFIX)
public class LocaleContextResolverGroupProperties implements BlueskyGroupProperties<LocaleContextResolverProperties> {

	private static final long serialVersionUID = 1L;
	
	@Autowired
	private LocaleContextResolverProperties parent;
	
	private Map<String, LocaleContextResolverProperties> groups = new HashMap<>();
	
	@Override
	public void load() {
		parentReload();
		var groupModuleInfoMap = BlueskyBootContextHolder.getContext().getGroupModuleInfoMap();
		
		BlueskyBootContextHolder.getContext().getGroupModules().keySet().forEach(groupName -> {
			var builder = groupModuleInfoMap.containsKey(groupName) ? groupModuleInfoMap.get(groupName).getLocaleContextResolverPropertiesBuilder() : LocaleContextResolverProperties.builder();
			
			// group이 없는 경우 기본 설정 추가
			if (!getGroups().containsKey(groupName)) {
				getGroups().put(groupName, builder.build());
			}
			
			var localeContextResolverProperties = getGroups().get(groupName);
			var propertyMapperConsumer = getParent().getPropertyMapperConsumer();
			propertyMapperConsumer.accept(getParent(), builder);
			propertyMapperConsumer.accept(localeContextResolverProperties, builder);
			
			getGroups().put(groupName, builder.build());
		});
	}

}
