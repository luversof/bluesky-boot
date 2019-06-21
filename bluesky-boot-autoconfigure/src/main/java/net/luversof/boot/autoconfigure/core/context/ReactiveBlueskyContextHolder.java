package net.luversof.boot.autoconfigure.core.context;

import java.util.function.Function;

import reactor.core.publisher.Mono;
import reactor.util.context.Context;

public class ReactiveBlueskyContextHolder {
	
	private static final Class<?> SECURITY_CONTEXT_KEY = BlueskyContext.class;
	
	/**
	 * Gets the {@code Mono<BlueskyContext>} from Reactor {@link Context}
	 * @return the {@code Mono<BlueskyContext>}
	 */
	public static Mono<BlueskyContext> getContext() {
		return Mono.subscriberContext()
			.filter( c -> c.hasKey(SECURITY_CONTEXT_KEY))
			.flatMap( c-> c.<Mono<BlueskyContext>>get(SECURITY_CONTEXT_KEY));
	}

	/**
	 * Clears the {@code Mono<BlueskyContext>} from Reactor {@link Context}
	 * @return Return a {@code Mono<Void>} which only replays complete and error signals
	 * from clearing the context.
	 */
	public static Function<Context, Context> clearContext() {
		return context -> context.delete(SECURITY_CONTEXT_KEY);
	}

	/**
	 * Creates a Reactor {@link Context} that contains the {@code Mono<BlueskyContext>}
	 * that can be merged into another {@link Context}
	 * @param BlueskyContext the {@code Mono<BlueskyContext>} to set in the returned
	 * Reactor {@link Context}
	 * @return a Reactor {@link Context} that contains the {@code Mono<BlueskyContext>}
	 */
	public static Context withBlueskyContext(Mono<? extends BlueskyContext> BlueskyContext) {
		return Context.of(SECURITY_CONTEXT_KEY, BlueskyContext);
	}
}
