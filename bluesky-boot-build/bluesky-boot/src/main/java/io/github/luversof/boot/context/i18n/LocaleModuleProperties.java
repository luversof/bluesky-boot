package io.github.luversof.boot.context.i18n;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.BlueskyModuleProperties;
import io.github.luversof.boot.util.function.SerializableFunction;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = "bluesky-boot.locale")
public class LocaleModuleProperties implements BlueskyModuleProperties<LocaleProperties>, BeanNameAware {
	
	private static final long serialVersionUID = 1L;
	
	public static final String DEFAULT_BEAN_NAME = "localeModuleProperties";
	public static final String EXTERNAL_LOCALE_BEAN_NAME = "externalLocaleModuleProperties";
	
	private String beanName;

	private LocaleProperties parent;
	
	private final SerializableFunction<String, LocaleProperties.LocalePropertiesBuilder> builderFunction;
	
	private Map<String, LocaleProperties> modules = new HashMap<>();
	
	public LocaleModuleProperties(LocaleProperties parent, SerializableFunction<String, LocaleProperties.LocalePropertiesBuilder> builderFunction) {
		this.parent = parent;
		this.builderFunction = builderFunction;
	}

	@Override
	public void load() {
		var propertyMapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
		
		BlueskyBootContextHolder.getContext().getModuleNameSet().forEach(moduleName -> {
			var builder = builderFunction.apply(moduleName);
			
			if (!getModules().containsKey(moduleName)) {
				getModules().put(moduleName, builder.build());
			}
			
			var localeProperties = getModules().get(moduleName);
			
			propertyMapper.from(getParent()::getBeanName).to(builder::beanName);
			propertyMapper.from(localeProperties::getBeanName).to(builder::beanName);
			
			propertyMapper.from(getParent()::getEnableLocaleList).whenNot(x -> x == null || x.isEmpty()).to(builder::enableLocaleList);
			propertyMapper.from(localeProperties::getEnableLocaleList).whenNot(x -> x == null || x.isEmpty()).to(builder::enableLocaleList);
			
			getModules().put(moduleName, builder.build());
			
		});
		
	}

}
