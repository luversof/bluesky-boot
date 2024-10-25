package io.github.luversof.boot.core;

public enum CoreResolveType {


	/**
	 * Used when handling module branching by domain
	 */
	DOMAIN,

	/**
	 * Used when handling branches by request path
	 */
	ADD_PATH_PATTERN,
	
	DOMAIN_ADD_PATH_PATTERN,

	/**
	 * Use if you have implemented a separate resolver
	 * 
	 * For cases like API servers that don't use domains, an additional implementation is provided to allow for branching.
	 */
	MODULE_NAME_RESOLVER;

}
