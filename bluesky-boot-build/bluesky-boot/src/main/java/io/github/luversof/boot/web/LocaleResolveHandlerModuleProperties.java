package io.github.luversof.boot.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.AbstractBlueskyModuleProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = LocaleResolveHandlerProperties.PREFIX)
public class LocaleResolveHandlerModuleProperties extends AbstractBlueskyModuleProperties<LocaleResolveHandlerProperties, LocaleResolveHandlerProperties.LocaleResolveHandlerPropertiesBuilder> implements BeanNameAware {
	
	private static final long serialVersionUID = 1L;

	private String beanName;

	private LocaleResolveHandlerProperties parent;
	
	private Map<String, LocaleResolveHandlerProperties> modules = new HashMap<>();
	
	public LocaleResolveHandlerModuleProperties(LocaleResolveHandlerProperties parent) {
		this.parent = parent;
	}
	
	@Override
	protected LocaleResolveHandlerProperties.LocaleResolveHandlerPropertiesBuilder getBuilder(String moduleName) {
		var moduleInfoMap = BlueskyBootContextHolder.getContext().getModuleInfoMap();
		return moduleInfoMap.containsKey(moduleName) ? moduleInfoMap.get(moduleName).getLocaleResolveHandlerPropertiesBuilder() : LocaleResolveHandlerProperties.builder();
	}
}
