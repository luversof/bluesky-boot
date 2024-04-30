package io.github.luversof.boot.core;

import io.github.luversof.boot.core.CoreProperties.CorePropertiesBuilder;

/**
 * enum to provide builders with a convenient way to provide coreProperties settings
 * @author bluesky
 *
 */
public interface CoreModuleInfo {

	/**
	 * Methods provided for writing builders to provide CoreModuleInfo
	 * @return CoreModulePropertiesBuilder
	 */
	CorePropertiesBuilder getBuilder();

}
