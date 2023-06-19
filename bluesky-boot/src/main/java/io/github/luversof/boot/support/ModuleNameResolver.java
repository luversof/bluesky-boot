package io.github.luversof.boot.support;

/**
 * resolver to use when specifying module branching criteria separately
 */
public interface ModuleNameResolver {
	
	/**
	 * 
	 * @return Target moduleName
	 */
	String resolve();
}
