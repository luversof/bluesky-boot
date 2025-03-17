package io.github.luversof.boot.context.i18n;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.util.function.SerializableSupplier;

@ConfigurationProperties(prefix = "bluesky-boot.external-locale")
public class ExternalLocaleProperties extends LocaleProperties {

	private static final long serialVersionUID = 1L;

	@Override
	protected SerializableSupplier<LocalePropertiesBuilder> getBuilderSupplier() {
		return () -> {
			var parentModuleInfo = BlueskyBootContextHolder.getContext().getParentModuleInfo();
			return parentModuleInfo == null ? LocaleProperties.builder() : parentModuleInfo.getExternalLocalePropertiesBuilder();
		};
	}

}
