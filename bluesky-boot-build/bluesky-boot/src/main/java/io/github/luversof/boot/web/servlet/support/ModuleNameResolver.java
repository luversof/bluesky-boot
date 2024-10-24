package io.github.luversof.boot.web.servlet.support;

import jakarta.servlet.http.HttpServletRequest;

@FunctionalInterface
public interface ModuleNameResolver {

	String resolve(HttpServletRequest request);

}
