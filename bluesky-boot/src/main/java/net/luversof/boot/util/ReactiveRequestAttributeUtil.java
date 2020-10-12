package net.luversof.boot.util;

import java.text.MessageFormat;

import reactor.core.publisher.Mono;

/**
 * reactive용 RequestAttributeUtil
 * 
 * @author bluesky
 *
 */
public class ReactiveRequestAttributeUtil {
	
	public static <T> Mono<T> setRequestAttribute(String name, T value) {
		return ServerWebExchangeUtil.getServerWebExchange().map(serverWebExchange -> {
			serverWebExchange.getAttributes().put(name, value);
			return value;
		});
	}

	public static <T> Mono<T> getRequestAttribute(String name) {
		return ServerWebExchangeUtil.getServerWebExchange().flatMap(serverWebExchange -> {
			if (!serverWebExchange.getAttributes().containsKey(name)) {
				return Mono.empty();
			}
			return Mono.just(serverWebExchange.getAttribute(name));
		});
	}

	public static String getAttributeName(String pattern, Object... arguments) {
		return MessageFormat.format(pattern, arguments);
	}
}
