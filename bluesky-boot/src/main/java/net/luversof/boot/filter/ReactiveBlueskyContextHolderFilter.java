package net.luversof.boot.filter;

import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;

import net.luversof.boot.context.BlueskyContext;
import net.luversof.boot.context.BlueskyContextImpl;
import net.luversof.boot.context.ReactiveBlueskyContextHolder;
import net.luversof.boot.util.ServerWebExchangeUtil;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

public class ReactiveBlueskyContextHolderFilter implements WebFilter {

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
		return chain.filter(exchange)
				.subscriberContext(c -> c.hasKey(BlueskyContext.class) ? c : withBlueskyContext(c, exchange));
	}

	private Context withBlueskyContext(Context mainContext, ServerWebExchange exchange) {
		return mainContext.putAll(load(exchange).as(ReactiveBlueskyContextHolder::withBlueskyContext));
	}

	private Mono<BlueskyContext> load(ServerWebExchange exchange) {
		return exchange.getSession().flatMap(attrs -> Mono
				.just(new BlueskyContextImpl(ServerWebExchangeUtil.getModulePropertiesEntry(exchange).getKey())));
	}

}
