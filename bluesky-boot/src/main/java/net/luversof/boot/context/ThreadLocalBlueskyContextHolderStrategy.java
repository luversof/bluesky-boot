package net.luversof.boot.context;

public class ThreadLocalBlueskyContextHolderStrategy extends AbstractBlueskyContextHolderStrategy {

	private static final ThreadLocal<BlueskyContext> contextHolder = new ThreadLocal<>();

	@Override
	protected ThreadLocal<BlueskyContext> getContextHolder() {
		return contextHolder;
	}

}
