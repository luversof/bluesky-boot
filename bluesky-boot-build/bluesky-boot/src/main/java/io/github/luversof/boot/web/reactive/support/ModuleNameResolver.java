package io.github.luversof.boot.web.reactive.support;

import org.springframework.web.server.ServerWebExchange;

@FunctionalInterface
public interface ModuleNameResolver {

	String resolve(ServerWebExchange exchange);

}
