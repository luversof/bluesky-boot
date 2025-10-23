package io.github.luversof.boot.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.AbstractBlueskyModuleProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = DomainProperties.PREFIX)
public class DomainModuleProperties extends AbstractBlueskyModuleProperties<DomainProperties, DomainProperties.DomainPropertiesBuilder> {

	private static final long serialVersionUID = 1L;

	@Setter(onMethod_ = { @Autowired })
	private DomainProperties parent;
	
	private Map<String, DomainProperties> modules = new HashMap<>();

	@Override
	protected DomainProperties.DomainPropertiesBuilder getBuilder(String moduleName) {
		var moduleInfoMap = BlueskyBootContextHolder.getContext().getModuleInfoMap();
		return moduleInfoMap.containsKey(moduleName) ? moduleInfoMap.get(moduleName).getDomainPropertiesBuilder() : DomainProperties.builder();
	}

}
