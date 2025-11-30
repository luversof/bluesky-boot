package io.github.luversof.boot.uuid;

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
@ConfigurationProperties(prefix = UuidGeneratorProperties.PREFIX)
public class UuidGeneratorModuleProperties extends AbstractBlueskyModuleProperties<UuidGeneratorProperties, UuidGeneratorProperties.UuidGeneratorPropertiesBuilder> {

	private static final long serialVersionUID = 1L;
	
	@Setter(onMethod_ = { @Autowired })
	private UuidGeneratorProperties parent;
	
	private Map<String, UuidGeneratorProperties> modules = new HashMap<>();

	@Override
	protected UuidGeneratorProperties.UuidGeneratorPropertiesBuilder getBuilder(String moduleName) {
		var moduleInfoMap = BlueskyBootContextHolder.getContext().getModuleInfoMap();
		return moduleInfoMap.containsKey(moduleName) ? moduleInfoMap.get(moduleName).getUuidGeneratorPropertiesBuilder() : UuidGeneratorProperties.builder();
	}
}
