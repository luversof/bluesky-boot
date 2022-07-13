package io.github.luversof.boot.context;

public interface BlueskyContextHolderStrategy {

	void clearContext();

	BlueskyContext getContext();
	
	void setContext(BlueskyContext context);

	BlueskyContext createEmptyContext();
	
}
