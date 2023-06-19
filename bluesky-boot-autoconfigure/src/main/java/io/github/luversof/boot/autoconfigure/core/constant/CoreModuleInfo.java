package io.github.luversof.boot.autoconfigure.core.constant;

import io.github.luversof.boot.autoconfigure.core.CoreProperties.CoreModuleProperties.CoreModulePropertiesBuilder;

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
	CoreModulePropertiesBuilder getBuilder();

}
