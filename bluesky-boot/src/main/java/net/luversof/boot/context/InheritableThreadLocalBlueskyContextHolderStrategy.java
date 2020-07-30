package net.luversof.boot.context;

public class InheritableThreadLocalBlueskyContextHolderStrategy extends AbstractBlueskyContextHolderStrategy {

	private static final ThreadLocal<BlueskyContext> contextHolder = new InheritableThreadLocal<>();

	@Override
	protected ThreadLocal<BlueskyContext> getContextHolder() {
		return contextHolder;
	}
	
}
