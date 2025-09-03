package io.github.luversof.boot.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.AbstractBlueskyGroupProperties;
import io.github.luversof.boot.util.function.SerializableFunction;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = CookieProperties.PREFIX)
public class CookieGroupProperties extends AbstractBlueskyGroupProperties<CookieProperties, CookieProperties.CookiePropertiesBuilder> implements BeanNameAware  {

	private static final long serialVersionUID = 1L;
	
	private String beanName;
	
	@Autowired
	@Qualifier(CookieProperties.DEFAULT_BEAN_NAME)
	private CookieProperties parent;
	
	private Map<String, CookieProperties> groups = new HashMap<>();
	
	protected SerializableFunction<String, CookieProperties.CookiePropertiesBuilder> getBuilderFunction() {
		return groupName -> {
			var groupModuleInfoMap = BlueskyBootContextHolder.getContext().getGroupModuleInfoMap();
			return groupModuleInfoMap.containsKey(groupName) ? groupModuleInfoMap.get(groupName).getCookiePropertiesBuilder() : CookieProperties.builder();
		};
	}
	
	@Override
	protected CookieProperties.CookiePropertiesBuilder getBuilder(String groupName) {
		return getBuilderFunction().apply(groupName);
	}
}
