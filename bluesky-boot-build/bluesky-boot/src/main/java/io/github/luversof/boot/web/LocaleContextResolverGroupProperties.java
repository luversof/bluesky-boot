package io.github.luversof.boot.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.AbstractBlueskyGroupProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = LocaleContextResolverProperties.PREFIX)
public class LocaleContextResolverGroupProperties extends AbstractBlueskyGroupProperties<LocaleContextResolverProperties, LocaleContextResolverProperties.LocaleContextResolverPropertiesBuilder> {

	private static final long serialVersionUID = 1L;
	
	@Autowired
	private LocaleContextResolverProperties parent;
	
	private Map<String, LocaleContextResolverProperties> groups = new HashMap<>();
	
	@Override
	protected LocaleContextResolverProperties.LocaleContextResolverPropertiesBuilder getBuilder(String groupName) {
		var groupModuleInfoMap = BlueskyBootContextHolder.getContext().getGroupModuleInfoMap();
		return groupModuleInfoMap.containsKey(groupName) ? groupModuleInfoMap.get(groupName).getLocaleContextResolverPropertiesBuilder() : LocaleContextResolverProperties.builder();
	}

}
