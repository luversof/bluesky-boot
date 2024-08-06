package io.github.luversof.boot.context.i18n;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.boot.context.properties.PropertyMapper;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.BlueskyModuleProperties;
import lombok.Data;

@Data
public class LocaleModuleProperties implements BlueskyModuleProperties<LocaleProperties> {
	
	public static final String DEFAULT_BEAN_NAME = "localeModuleProperties";

	private final LocaleProperties parent;
	
	private final Function<String, LocaleProperties.LocalePropertiesBuilder> builderFunction;
	
	private Map<String, LocaleProperties> modules = new HashMap<>();

	@Override
	public void load() {
		
		var propertyMapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
		
		BlueskyBootContextHolder.getContext().getModuleNameSet().forEach(moduleName -> {
			var builder = builderFunction.apply(moduleName);
			
			if (!getModules().containsKey(moduleName)) {
				getModules().put(moduleName, builder.build());
			}
			
			var localeProperties = getModules().get(moduleName);
			
			propertyMapper.from(getParent()::getEnableLocaleList).whenNot(x -> x == null || x.isEmpty()).to(builder::enableLocaleList);
			propertyMapper.from(localeProperties::getEnableLocaleList).whenNot(x -> x == null || x.isEmpty()).to(builder::enableLocaleList);
			
			getModules().put(moduleName, builder.build());
			
		});
		
	}

}
