package io.github.luversof.boot.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.AbstractBlueskyGroupProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = LocaleResolveHandlerProperties.PREFIX)
public class LocaleResolveHandlerGroupProperties extends AbstractBlueskyGroupProperties<LocaleResolveHandlerProperties, LocaleResolveHandlerProperties.LocaleResolveHandlerPropertiesBuilder> implements BeanNameAware {
	
	private static final long serialVersionUID = 1L;

	private String beanName;

	private LocaleResolveHandlerProperties parent;
	
	private Map<String, LocaleResolveHandlerProperties> groups = new HashMap<>();
	
	public LocaleResolveHandlerGroupProperties(LocaleResolveHandlerProperties parent) {
		this.parent = parent;
	}
	
	@Override
	protected LocaleResolveHandlerProperties.LocaleResolveHandlerPropertiesBuilder getBuilder(String groupName) {
		var groupModuleInfoMap = BlueskyBootContextHolder.getContext().getGroupModuleInfoMap();
		return groupModuleInfoMap.containsKey(groupName) ? groupModuleInfoMap.get(groupName).getLocaleResolveHandlerPropertiesBuilder() : LocaleResolveHandlerProperties.builder();
	}
}
