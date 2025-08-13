package io.github.luversof.boot.web;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.util.function.SerializableSupplier;


@ConfigurationProperties(prefix = "bluesky-boot.web.external-cookie")
public class ExternalCookieProperties extends CookieProperties {

	private static final long serialVersionUID = 1L;

	@Override
	protected SerializableSupplier<CookiePropertiesBuilder> getBuilderSupplier() {
		return () -> {
			var parentModuleInfo = BlueskyBootContextHolder.getContext().getParentModuleInfo();
			return parentModuleInfo == null ? CookieProperties.builder() : parentModuleInfo.getExternalCookiePropertiesBuilder();
		};
	}
	
	

}
