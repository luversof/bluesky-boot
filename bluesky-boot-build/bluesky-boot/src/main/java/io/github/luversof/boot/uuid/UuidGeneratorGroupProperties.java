package io.github.luversof.boot.uuid;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.AbstractBlueskyGroupProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = UuidGeneratorProperties.PREFIX)
public class UuidGeneratorGroupProperties extends AbstractBlueskyGroupProperties<UuidGeneratorProperties, UuidGeneratorProperties.UuidGeneratorPropertiesBuilder> {

	private static final long serialVersionUID = 1L;
	
	@Setter(onMethod_ = { @Autowired })
	private UuidGeneratorProperties parent;
	
	private Map<String, UuidGeneratorProperties> groups = new HashMap<>();
	
	@Override
	protected UuidGeneratorProperties.UuidGeneratorPropertiesBuilder getBuilder(String groupName) {
		var groupModuleInfoMap = BlueskyBootContextHolder.getContext().getGroupModuleInfoMap();
		return groupModuleInfoMap.containsKey(groupName) ? groupModuleInfoMap.get(groupName).getUuidGeneratorPropertiesBuilder() : UuidGeneratorProperties.builder();
	}

}
