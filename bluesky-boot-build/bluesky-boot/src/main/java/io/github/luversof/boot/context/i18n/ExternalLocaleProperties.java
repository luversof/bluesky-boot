package io.github.luversof.boot.context.i18n;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.util.function.SerializableSupplier;

@ConfigurationProperties(prefix = ExternalLocaleProperties.PREFIX)
public class ExternalLocaleProperties extends LocaleProperties {

	private static final long serialVersionUID = 1L;
	
	public static final String PREFIX = "bluesky-boot.external-locale";

	@Override
	protected SerializableSupplier<LocalePropertiesBuilder> getBuilderSupplier() {
		return () -> {
			var parentModuleInfo = BlueskyBootContextHolder.getContext().getParentModuleInfo();
			return parentModuleInfo == null ? LocaleProperties.builder() : parentModuleInfo.getExternalLocalePropertiesBuilder();
		};
	}

}
