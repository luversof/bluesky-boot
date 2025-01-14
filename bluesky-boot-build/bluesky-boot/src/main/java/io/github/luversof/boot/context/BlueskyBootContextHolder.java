package io.github.luversof.boot.context;

/**
 * Holder of BlueskyBootContext
 */
public final class BlueskyBootContextHolder {
	
	/**
	 * Handling utility class constructors
	 */
	private BlueskyBootContextHolder() {}
	
	private static BlueskyBootContext contextHolder;

	/**
	 * Return BlueskyBootContext
	 * 
	 * @return BlueskyBootContext
	 */
	public static BlueskyBootContext getContext() {
		if (contextHolder == null) {
			contextHolder = new BlueskyBootContext();
		}
		return contextHolder;
	}

}
