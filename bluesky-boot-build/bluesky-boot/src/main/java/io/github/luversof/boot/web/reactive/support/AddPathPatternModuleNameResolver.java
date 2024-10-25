package io.github.luversof.boot.web.reactive.support;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map.Entry;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;
import org.springframework.web.server.ServerWebExchange;

import io.github.luversof.boot.web.DomainModuleProperties;
import io.github.luversof.boot.web.DomainProperties;

public class AddPathPatternModuleNameResolver extends AbstractModuleNameResolver {
	
	private final PathMatcher pathMatcher = new AntPathMatcher();
	private final Comparator<Entry<String, DomainProperties>> comparator = (o1, o2) -> Integer.compare(o1.getValue().getAddPathPatterns()[0].length(), o2.getValue().getAddPathPatterns()[0].length());

	@Override
	protected Entry<String, DomainProperties> getModulePropertiesEntry(ServerWebExchange exchange, DomainModuleProperties domainModuleProperties) {
		var applicationContext = exchange.getApplicationContext();
		Assert.notNull(applicationContext, APPLICATION_CONTEXT_MUST_EXIST);
		return domainModuleProperties.getModules().entrySet().stream().filter(moduleEntry -> Arrays.asList(moduleEntry.getValue().getAddPathPatterns()).stream().anyMatch(addPathPattern -> pathMatcher.match(addPathPattern, exchange.getRequest().getURI().getPath()))).sorted(comparator.reversed()).findFirst().orElse(null);
	}
	
}
