package io.github.luversof.boot.context;

/**
 * A context that contains the moduleName per request
 */
@FunctionalInterface
public interface BlueskyContext {
	
	/**
	 * Get module name
	 * 
	 * @return moduleName
	 */
	String getModuleName();

}
