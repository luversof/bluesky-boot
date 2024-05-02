package io.github.luversof.boot.context;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BlueskyBootContextHolder {
	
	private static BlueskyBootContext CONTEXT_HOLDER;

	public static BlueskyBootContext getContext() {
		if (CONTEXT_HOLDER == null) {
			CONTEXT_HOLDER = new BlueskyBootContext();
		}
		return CONTEXT_HOLDER;
	}

}
