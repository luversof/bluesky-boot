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
@ConfigurationProperties(prefix = WebProperties.PREFIX)
public class WebGroupProperties extends AbstractBlueskyGroupProperties<WebProperties, WebProperties.WebPropertiesBuilder> {

	private static final long serialVersionUID = 1L;
	
	@Setter(onMethod_ = { @Autowired })
	private WebProperties parent;
	
	private Map<String, WebProperties> groups = new HashMap<>();
	
	@Override
	protected WebProperties.WebPropertiesBuilder getBuilder(String groupName) {
		var groupModuleInfoMap = BlueskyBootContextHolder.getContext().getGroupModuleInfoMap();
		return groupModuleInfoMap.containsKey(groupName) ? groupModuleInfoMap.get(groupName).getWebPropertiesBuilder() : WebProperties.builder();
	}

}
