package io.github.luversof.boot.context.i18n;

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
@ConfigurationProperties(prefix = LocaleProperties.PREFIX)
public class LocaleGroupProperties extends AbstractBlueskyGroupProperties<LocaleProperties, LocaleProperties.LocalePropertiesBuilder> implements BeanNameAware {
	
	private static final long serialVersionUID = 1L;
	
	private String beanName;

	@Autowired
	@Qualifier(LocaleProperties.DEFAULT_BEAN_NAME)
	private LocaleProperties parent;
	
	private Map<String, LocaleProperties> groups = new HashMap<>();
	
	protected SerializableFunction<String, LocaleProperties.LocalePropertiesBuilder> getBuilderFunction() {
		return groupName -> {
			var groupModuleInfoMap = BlueskyBootContextHolder.getContext().getGroupModuleInfoMap();
			return groupModuleInfoMap.containsKey(groupName) ? groupModuleInfoMap.get(groupName).getLocalePropertiesBuilder() : LocaleProperties.builder();
		};
	}
	
	@Override
	protected LocaleProperties.LocalePropertiesBuilder getBuilder(String groupName) {
		return getBuilderFunction().apply(groupName);
	}
}
