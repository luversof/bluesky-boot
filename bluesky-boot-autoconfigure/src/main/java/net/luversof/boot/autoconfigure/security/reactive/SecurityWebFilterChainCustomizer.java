package net.luversof.boot.autoconfigure.security.reactive;

import org.springframework.security.config.web.server.ServerHttpSecurity;

public interface SecurityWebFilterChainCustomizer {

	default void preConfigure(ServerHttpSecurity http) {
	}

	default void postConfigure(ServerHttpSecurity http) {
	}

}
