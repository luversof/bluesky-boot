package io.github.luversof.boot.web.reactive.support;

import java.util.Map.Entry;

import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;

import io.github.luversof.boot.web.DomainModuleProperties;
import io.github.luversof.boot.web.DomainProperties;

public abstract class AbstractModuleNameResolver implements ModuleNameResolver {
	
	protected static final String APPLICATION_CONTEXT_MUST_EXIST = "ApplicationContext must exist";

	@Override
	public String resolve(ServerWebExchange exchange) {
		var modulePropertiesEntry = getModulePropertiesEntry(exchange);
		return modulePropertiesEntry == null ? null : modulePropertiesEntry.getKey();
	}
	
	public Entry<String, DomainProperties> getModulePropertiesEntry(ServerWebExchange exchange) {
		var applicationContext = exchange.getApplicationContext();
		Assert.notNull(applicationContext, APPLICATION_CONTEXT_MUST_EXIST);
		var domainModuleProperties = applicationContext.getBean(DomainModuleProperties.class);
		Assert.notEmpty(domainModuleProperties.getModules(), "domainModuleProperties is not set");
		
		var modules = domainModuleProperties.getModules();
		if (modules.size() == 1) {
			return modules.entrySet().stream().findAny().orElse(null);
		}
		
		var module = getModulePropertiesEntry(exchange, domainModuleProperties);
		
		if (module == null) {
			module= modules.entrySet().stream().findFirst().orElse(null);
		}
		return module;
	}
	
	protected abstract Entry<String, DomainProperties> getModulePropertiesEntry(ServerWebExchange exchange, DomainModuleProperties domainModuleProperties);
}
