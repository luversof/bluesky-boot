package io.github.luversof.boot.web.filter;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import io.github.luversof.boot.context.BlueskyContext;
import io.github.luversof.boot.context.ReactiveBlueskyContextHolder;
import io.github.luversof.boot.util.ServerWebExchangeUtil;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

public class ReactiveBlueskyContextHolderFilter implements WebFilter {

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		return chain.filter(exchange)
				.contextWrite(context -> context.hasKey(BlueskyContext.class) ? context : withBlueskyContext(context, exchange));
	}

	private Context withBlueskyContext(Context mainContext, ServerWebExchange exchange) {
		return mainContext.putAll(load(exchange).as(ReactiveBlueskyContextHolder::withBlueskyContext).readOnly());
	}

	private Mono<BlueskyContext> load(ServerWebExchange exchange) {
		return exchange.getSession().flatMap(attrs -> Mono
				.just(() -> ServerWebExchangeUtil.getModulePropertiesEntry(exchange).getKey()));
	}

}
