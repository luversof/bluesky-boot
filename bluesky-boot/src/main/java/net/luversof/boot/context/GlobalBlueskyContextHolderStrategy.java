package net.luversof.boot.context;

import org.springframework.util.Assert;

public class GlobalBlueskyContextHolderStrategy implements BlueskyContextHolderStrategy<BlueskyContext> {

	private static BlueskyContext contextHolder;

	@Override
	public void clearContext() {
		contextHolder = null;
	}

	@Override
	public BlueskyContext getContext() {
		if (contextHolder == null) {
			contextHolder = createEmptyContext();
		}

		return contextHolder;
	}

	@Override
	public void setContext(BlueskyContext context) {
		Assert.notNull(context, "Only non-null BlueskyContext instances are permitted");
		contextHolder = context;
	}

	@Override
	public BlueskyContext createEmptyContext() {
		return new BlueskyContextImpl();
	}

	@Override
	public boolean hasContext() {
		return contextHolder != null;
	}
}
