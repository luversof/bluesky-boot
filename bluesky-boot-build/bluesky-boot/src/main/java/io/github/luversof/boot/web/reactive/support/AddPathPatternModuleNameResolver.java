package io.github.luversof.boot.web.reactive.support;

import java.util.Arrays;
import java.util.Map.Entry;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;

import io.github.luversof.boot.web.DomainModuleProperties;
import io.github.luversof.boot.web.DomainProperties;

public class AddPathPatternModuleNameResolver extends AbstractModuleNameResolver {
	
	private static final PathMatcher pathMatcher = new AntPathMatcher();

	@Override
	protected Entry<String, DomainProperties> getModulePropertiesEntry(ServerWebExchange exchange, DomainModuleProperties domainModuleProperties) {
		var applicationContext = exchange.getApplicationContext();
		Assert.notNull(applicationContext, APPLICATION_CONTEXT_MUST_EXIST);
		return domainModuleProperties.getModules().entrySet().stream().filter(moduleEntry -> Arrays.asList(moduleEntry.getValue().getAddPathPatterns()).stream().anyMatch(addPathPattern -> pathMatcher.match(addPathPattern, exchange.getRequest().getURI().getPath()))).findAny().orElse(null);
	}
	
}
