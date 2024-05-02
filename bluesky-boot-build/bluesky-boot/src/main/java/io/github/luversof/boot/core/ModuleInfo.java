package io.github.luversof.boot.core;

import io.github.luversof.boot.core.CoreProperties.CorePropertiesBuilder;
import io.github.luversof.boot.context.i18n.LocaleProperties.LocalePropertiesBuilder;

/**
 * enum to provide builders with a convenient way to provide coreProperties settings
 * @author bluesky
 *
 */
public interface ModuleInfo {

	/**
	 * Methods provided for writing builders to provide CoreModuleInfo
	 * @return CoreModulePropertiesBuilder
	 */
	CorePropertiesBuilder getCoreBuilder();
	
	LocalePropertiesBuilder getLocaleBuilder();

}
