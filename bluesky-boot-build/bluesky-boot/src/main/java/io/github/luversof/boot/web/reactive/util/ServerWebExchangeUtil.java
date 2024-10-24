package io.github.luversof.boot.web.reactive.util;

import org.springframework.util.Assert;
import org.springframework.web.filter.reactive.ServerWebExchangeContextFilter;
import org.springframework.web.server.ServerWebExchange;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ServerWebExchangeUtil {
	
	private static final String IPV4_PATTERN = "(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
	
	public static final String EXCHANGE_CONTEXT_ATTRIBUTE = ServerWebExchangeContextFilter.class.getName()	+ ".EXCHANGE_CONTEXT";
	

	public static Mono<ServerWebExchange> getServerWebExchange() {
		return Mono.deferContextual(Mono::just)
				.cast(Context.class)
				.filter(c -> c.hasKey(EXCHANGE_CONTEXT_ATTRIBUTE))
				.flatMap(c -> Mono.just(c.get(EXCHANGE_CONTEXT_ATTRIBUTE)));
	}
	
	public static boolean isInternalRequest(ServerWebExchange exchange) {
		var headers = exchange.getRequest().getHeaders();
		Assert.notNull(headers, "header value must exist");
		var host = headers.getHost();
		Assert.notNull(host, "header host value must exist");
		var hostName = host.getHostName();
		if (hostName.equals("localhost")) {
			return true;
		}
		return hostName.matches(IPV4_PATTERN);
	}
	
}
