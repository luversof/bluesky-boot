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
@ConfigurationProperties(prefix = WebProperties.PREFIX)
public class WebModuleProperties  extends AbstractBlueskyModuleProperties<WebProperties, WebProperties.WebPropertiesBuilder> {
	
	private static final long serialVersionUID = 1L;

	@Autowired
	private WebProperties parent;
	
	private Map<String, WebProperties> modules = new HashMap<>();
	
	@Override
	protected WebProperties.WebPropertiesBuilder getBuilder(String moduleName) {
		var moduleInfoMap = BlueskyBootContextHolder.getContext().getModuleInfoMap();
		return moduleInfoMap.containsKey(moduleName) ? moduleInfoMap.get(moduleName).getWebPropertiesBuilder() : WebProperties.builder();
	}
}
