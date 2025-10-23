package io.github.luversof.boot.web;

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
@ConfigurationProperties(prefix = DomainProperties.PREFIX)
public class DomainGroupProperties extends AbstractBlueskyGroupProperties<DomainProperties, DomainProperties.DomainPropertiesBuilder> {

	private static final long serialVersionUID = 1L;
	
	@Setter(onMethod_ = { @Autowired })
	private DomainProperties parent;
	
	private Map<String, DomainProperties> groups = new HashMap<>();
	
	@Override
	protected DomainProperties.DomainPropertiesBuilder getBuilder(String groupName) {
		var groupModuleInfoMap = BlueskyBootContextHolder.getContext().getGroupModuleInfoMap();
		return groupModuleInfoMap.containsKey(groupName) ? groupModuleInfoMap.get(groupName).getDomainPropertiesBuilder() : DomainProperties.builder();
	}
}
