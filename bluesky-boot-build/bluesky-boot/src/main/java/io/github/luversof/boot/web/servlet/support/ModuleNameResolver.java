package io.github.luversof.boot.web.servlet.support;

import jakarta.servlet.http.HttpServletRequest;

@FunctionalInterface
public interface ModuleNameResolver {

	/**
	 * Resolve moduleName based on HttpServletRequest
	 * 
	 * @param request HttpServletRequest
	 * @return resolved moduleName
	 */
	String resolve(HttpServletRequest request);

}
