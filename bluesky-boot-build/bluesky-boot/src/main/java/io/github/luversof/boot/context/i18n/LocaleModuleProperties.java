package io.github.luversof.boot.context.i18n;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.BlueskyModuleProperties;
import lombok.Data;
import lombok.Setter;

@Data
@ConfigurationProperties(prefix = "bluesky-boot.locale")
public class LocaleModuleProperties implements BlueskyModuleProperties<LocaleProperties> {

	@Setter(onMethod_ = { @Autowired })
	private LocaleProperties parent;
	
	private Map<String, LocaleProperties> modules = new HashMap<>();

	@Override
	public void afterPropertiesSet() throws Exception {
		
		var moduleInfoMap = BlueskyBootContextHolder.getContext().getModuleInfoMap();
		
		var propertyMapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
		
		BlueskyBootContextHolder.getContext().getModuleNameSet().forEach(moduleName -> {
			var builder = moduleInfoMap.containsKey(moduleName) ?  moduleInfoMap.get(moduleName).getLocalePropertiesBuilder() : LocaleProperties.builder();
			
			if (!getModules().containsKey(moduleName)) {
				getModules().put(moduleName, builder.build());
			}
			
			var localeModuleProperties = getModules().get(moduleName);
			
			propertyMapper.from(getParent()::getDefaultLocale).to(builder::defaultLocale);
			propertyMapper.from(localeModuleProperties::getDefaultLocale).to(builder::defaultLocale);
			propertyMapper.from(getParent()::getEnableLocaleList).to(builder::enableLocaleList);
			propertyMapper.from(localeModuleProperties::getEnableLocaleList).to(builder::enableLocaleList);
			
			getModules().put(moduleName, builder.build());
			
		});
		
	}

}
