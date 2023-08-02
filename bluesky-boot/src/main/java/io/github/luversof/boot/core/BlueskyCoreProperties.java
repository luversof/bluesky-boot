package io.github.luversof.boot.core;

/**
 * The properties objects that extend BlueskyProperties and are used by the required properties
 * @author bluesky
 *
 * @param <T>
 */
public interface BlueskyCoreProperties<T extends BlueskyCoreModuleProperties> extends BlueskyProperties<T> {
	
	/**
	 * Define how module calls are handled
	 * 
	 * [domain , addPathPattern, moduleNameResolver]
	 */
	CoreModulePropertiesResolveType getResolveType();
	void setResolveType(CoreModulePropertiesResolveType resolveType);
	
	
	public enum CoreModulePropertiesResolveType {

		/**
		 * Used when handling module branching by domain
		 */
		DOMAIN,

		/**
		 * Used when handling branches by request path
		 */
		ADD_PATH_PATTERN,

		/**
		 * Use if you have implemented a separate resolver
		 * 
		 * For cases like API servers that don't use domains, an additional implementation is provided to allow for branching.
		 */
		MODULE_NAME_RESOLVER;
	}

}
