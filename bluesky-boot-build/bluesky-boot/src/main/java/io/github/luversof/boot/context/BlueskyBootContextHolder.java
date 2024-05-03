package io.github.luversof.boot.context;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BlueskyBootContextHolder {
	
	private static BlueskyBootContext contextHolder;

	public static BlueskyBootContext getContext() {
		if (contextHolder == null) {
			contextHolder = new BlueskyBootContext();
		}
		return contextHolder;
	}

}
