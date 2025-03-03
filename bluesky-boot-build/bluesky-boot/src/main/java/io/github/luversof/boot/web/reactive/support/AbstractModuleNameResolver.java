package io.github.luversof.boot.web.reactive.support;

import java.util.Map.Entry;

import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;

import io.github.luversof.boot.web.DomainModuleProperties;
import io.github.luversof.boot.web.DomainProperties;

/**
 * Abstract classes of DomainModuleNameResolver and AddPathPatternModuleNameResolver
 */
public abstract class AbstractModuleNameResolver implements ModuleNameResolver {
	
	/**
	 * ApplicationContext null error message
	 */
	protected static final String ERROR_MESSAGE_NOT_NULL_APPLICATION_CONTEXT = "ApplicationContext must exist";
	
	@Override
	public String resolve(ServerWebExchange exchange) {
		var modulePropertiesEntry = getModulePropertiesEntry(exchange);
		return modulePropertiesEntry == null ? null : modulePropertiesEntry.getKey();
	}
	
	/**
	 * Find module Entry matching the current request
	 * 
	 * @param exchange ServerWebExchange
	 * @return Entry of DomainProperties
	 */
	public Entry<String, DomainProperties> getModulePropertiesEntry(ServerWebExchange exchange) {
		var applicationContext = exchange.getApplicationContext();
		Assert.notNull(applicationContext, ERROR_MESSAGE_NOT_NULL_APPLICATION_CONTEXT);
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
	
	/**
	 * Method that returns a DomainProperties Entry corresponding to the request.
	 * @param exchange ServerWebExchange
	 * @param domainModuleProperties DomainProperties
	 * @return
	 */
	protected abstract Entry<String, DomainProperties> getModulePropertiesEntry(ServerWebExchange exchange, DomainModuleProperties domainModuleProperties);
}
