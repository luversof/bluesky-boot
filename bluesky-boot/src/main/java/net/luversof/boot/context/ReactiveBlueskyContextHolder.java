package net.luversof.boot.context;

import java.util.function.Function;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.util.context.Context;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReactiveBlueskyContextHolder {

	private static final Class<?> BLUESKY_CONTEXT_KEY = BlueskyContext.class;

	public static Mono<BlueskyContext> getContext() {
		return Mono.subscriberContext().filter(c -> c.hasKey(BLUESKY_CONTEXT_KEY))
				.flatMap(c -> c.<Mono<BlueskyContext>>get(BLUESKY_CONTEXT_KEY));
	}

	public static Function<Context, Context> clearContext() {
		return context -> context.delete(BLUESKY_CONTEXT_KEY);
	}

	public static Context withBlueskyContext(Mono<? extends BlueskyContext> blueskyContext) {
		return Context.of(BLUESKY_CONTEXT_KEY, blueskyContext);
	}
}
