package io.github.luversof.boot.context.i18n;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.context.i18n.LocaleProperties.LocalePropertiesBuilder;
import io.github.luversof.boot.util.function.SerializableFunction;

@ConfigurationProperties(prefix = "bluesky-boot.external-locale")
public class ExternalLocaleModuleProperties extends LocaleModuleProperties {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Dependency 참조 순서 처리를 위해 선언
	 */
	@Autowired
	public void setExternalLocaleProperties(ExternalLocaleProperties externalLocaleProperties) {
		setParent(externalLocaleProperties);
	}

	@Override
	protected SerializableFunction<String, LocalePropertiesBuilder> getBuilderFunction() {
		return moduleName -> {
			var moduleInfoMap = BlueskyBootContextHolder.getContext().getModuleInfoMap();
			return moduleInfoMap.containsKey(moduleName) ? moduleInfoMap.get(moduleName).getExternalLocalePropertiesBuilder() : LocaleProperties.builder();
		};
	}
	
	

}
