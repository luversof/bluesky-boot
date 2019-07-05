package net.luversof.boot.autoconfigure.core.context;

import org.springframework.util.Assert;

public class GlobalBlueskyContextHolderStrategy implements BlueskyContextHolderStrategy {

	private static BlueskyContext contextHolder;

	// ~ Methods
	// ========================================================================================================

	public void clearContext() {
		contextHolder = null;
	}

	public BlueskyContext getContext() {
		if (contextHolder == null) {
			contextHolder = createEmptyContext();
		}

		return contextHolder;
	}

	public void setContext(BlueskyContext context) {
		Assert.notNull(context, "Only non-null BlueskyContext instances are permitted");
		contextHolder = context;
	}

	public BlueskyContext createEmptyContext() {
		return new BlueskyContextImpl();
	}
}
