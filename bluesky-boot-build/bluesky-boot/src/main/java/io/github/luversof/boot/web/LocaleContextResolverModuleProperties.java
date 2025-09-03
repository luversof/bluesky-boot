package io.github.luversof.boot.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.AbstractBlueskyModuleProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = LocaleContextResolverProperties.PREFIX)
public class LocaleContextResolverModuleProperties extends AbstractBlueskyModuleProperties<LocaleContextResolverProperties, LocaleContextResolverProperties.LocaleContextResolverPropertiesBuilder> {
	
	private static final long serialVersionUID = 1L;

	@Autowired
	private LocaleContextResolverProperties parent;
	
	private Map<String, LocaleContextResolverProperties> modules = new HashMap<>();

	@Override
	protected LocaleContextResolverProperties.LocaleContextResolverPropertiesBuilder getBuilder(String moduleName) {
		var moduleInfoMap = BlueskyBootContextHolder.getContext().getModuleInfoMap();
		return moduleInfoMap.containsKey(moduleName) ? moduleInfoMap.get(moduleName).getLocaleContextResolverPropertiesBuilder() : LocaleContextResolverProperties.builder();
	}
	
}
