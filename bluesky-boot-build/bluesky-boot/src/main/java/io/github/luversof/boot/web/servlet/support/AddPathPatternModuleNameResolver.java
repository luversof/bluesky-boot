package io.github.luversof.boot.web.servlet.support;

import java.util.Arrays;
import java.util.Map.Entry;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import io.github.luversof.boot.web.DomainModuleProperties;
import io.github.luversof.boot.web.DomainProperties;
import jakarta.servlet.http.HttpServletRequest;

public class AddPathPatternModuleNameResolver extends AbstractModuleNameResolver {
	
	private final PathMatcher pathMatcher = new AntPathMatcher();

	@Override
	protected Entry<String, DomainProperties> getModulePropertiesEntry(HttpServletRequest request, DomainModuleProperties domainModuleProperties) {
		return domainModuleProperties.getModules().entrySet().stream().filter(moduleEntry -> Arrays.asList(moduleEntry.getValue().getAddPathPatterns()).stream().anyMatch(addPathPattern -> pathMatcher.match(addPathPattern, request.getServletPath()))).findAny().orElse(null);
	}
	
}
