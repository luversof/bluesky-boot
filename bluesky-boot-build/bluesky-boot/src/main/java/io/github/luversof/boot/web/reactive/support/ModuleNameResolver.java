
package io.github.luversof.boot.web.reactive.support;
import org.springframework.web.server.ServerWebExchange;

@FunctionalInterface
public interface ModuleNameResolver {

	/**
	 * Resolve moduleName based on ServerWebExchange
	 * 
	 * @param exchange ServerWebExchange
	 * @return resolved moduleName
	 */
	String resolve(ServerWebExchange exchange);

}
