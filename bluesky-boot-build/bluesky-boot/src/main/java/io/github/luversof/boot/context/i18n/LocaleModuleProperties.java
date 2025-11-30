package io.github.luversof.boot.context.i18n;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.AbstractBlueskyModuleProperties;
import io.github.luversof.boot.util.function.SerializableFunction;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = LocaleProperties.PREFIX)
public class LocaleModuleProperties extends AbstractBlueskyModuleProperties<LocaleProperties, LocaleProperties.LocalePropertiesBuilder> implements BeanNameAware {
	
	private static final long serialVersionUID = 1L;
	
	public static final String DEFAULT_BEAN_NAME = "bluesky-boot.locale-io.github.luversof.boot.context.i18n.LocaleModuleProperties";
	public static final String EXTERNAL_LOCALE_BEAN_NAME = "bluesky-boot.external-locale-io.github.luversof.boot.context.i18n.ExternalLocaleModuleProperties";
	
	private String beanName;

	private LocaleProperties parent;
	
	@Autowired
	public void setParent(@Qualifier(LocaleProperties.DEFAULT_BEAN_NAME) LocaleProperties localeProperties) {
		this.parent = localeProperties;
	}
	
	private Map<String, LocaleProperties> modules = new HashMap<>();
	
	protected SerializableFunction<String, LocaleProperties.LocalePropertiesBuilder> getBuilderFunction() {
		return moduleName -> {
			var moduleInfoMap = BlueskyBootContextHolder.getContext().getModuleInfoMap();
			return moduleInfoMap.containsKey(moduleName) ? moduleInfoMap.get(moduleName).getLocalePropertiesBuilder() : LocaleProperties.builder();
		};
	}

	@Override
	protected LocaleProperties.LocalePropertiesBuilder getBuilder(String moduleName) {
		return getBuilderFunction().apply(moduleName);
	}
	
}
