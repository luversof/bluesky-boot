package net.luversof.boot.autoconfigure.core.context;

public interface BlueskyContextHolderStrategy {

	void clearContext();

	/**
	 * Obtains the current context.
	 *
	 * @return a context (never <code>null</code> - create a default implementation if
	 * necessary)
	 */
	BlueskyContext getContext();

	void setContext(BlueskyContext context);

	BlueskyContext createEmptyContext();
}
