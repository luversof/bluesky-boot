package net.luversof.boot.autoconfigure.core.context;

import org.springframework.util.Assert;

public class BlueskyContextHolder {

	private static final ThreadLocal<BlueskyContext> contextHolder = new ThreadLocal<>();

	public static BlueskyContext getContext() {
		BlueskyContext blueskyContext = contextHolder.get();

		if (blueskyContext == null) {
			blueskyContext = createEmptyContext();
			contextHolder.set(blueskyContext);
		}
		return blueskyContext;
	}

	private static BlueskyContext createEmptyContext() {
		return new BlueskyContextImpl();
	}

	public static void setContext(BlueskyContext blueskyContext) {
		Assert.notNull(blueskyContext, "Only non-null BlueskyContext instances are permitted");
		contextHolder.set(blueskyContext);
	}

	public static void clearContext() {
		contextHolder.remove();
	}

}
