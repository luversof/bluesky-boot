package io.github.luversof.boot.context;

public interface BlueskyContextHolderStrategy<T> {

	void clearContext();

	T getContext();
	
	boolean hasContext();

	void setContext(T context);

	default T createEmptyContext() {
		return null;
	}
}
