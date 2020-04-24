package net.luversof.boot.autoconfigure.core.context;

import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BlueskyContextHolder {
	
	public static final String MODE_THREADLOCAL = "MODE_THREADLOCAL";
	public static final String MODE_INHERITABLETHREADLOCAL = "MODE_INHERITABLETHREADLOCAL";
	public static final String MODE_GLOBAL = "MODE_GLOBAL";
	public static final String SYSTEM_PROPERTY = "bluesky.context.strategy";
	private static String strategyName = System.getProperty(SYSTEM_PROPERTY);
	private static BlueskyContextHolderStrategy strategy;
	private static int initializeCount = 0;
	
	static {
		initialize();
	}

	public static void clearContext() {
		strategy.clearContext();
	}

	public static BlueskyContext getContext() {
		return strategy.getContext();
	}

	public static int getInitializeCount() {
		return initializeCount;
	}

	private static void initialize() {
		if (!StringUtils.hasText(strategyName)) {
			// Set default
			strategyName = MODE_THREADLOCAL;
		}

		if (strategyName.equals(MODE_THREADLOCAL)) {
			strategy = new ThreadLocalBlueskyContextHolderStrategy();
		}
		else if (strategyName.equals(MODE_INHERITABLETHREADLOCAL)) {
			strategy = new InheritableThreadLocalBlueskyContextHolderStrategy();
		}
		else if (strategyName.equals(MODE_GLOBAL)) {
			strategy = new GlobalBlueskyContextHolderStrategy();
		}
		else {
			// Try to load a custom strategy
			try {
				var clazz = Class.forName(strategyName);
				var customStrategy = clazz.getConstructor();
				strategy = (BlueskyContextHolderStrategy) customStrategy.newInstance();
			}
			catch (Exception ex) {
				ReflectionUtils.handleReflectionException(ex);
			}
		}

		initializeCount++;
	}
	
	public static void setContext(BlueskyContext context) {
		strategy.setContext(context);
	}
	
	public static void setContext(String moduleName) {
		setContext(new BlueskyContextImpl(moduleName));
	}
	
	public static void setStrategyName(String strategyName) {
		BlueskyContextHolder.strategyName = strategyName;
		initialize();
	}
	
	public static BlueskyContextHolderStrategy getContextHolderStrategy() {
		return strategy;
	}

	public static BlueskyContext createEmptyContext() {
		return strategy.createEmptyContext();
	}
	
	@Override
	public String toString() {
		return "BlueskyContextHolder[strategy='" + strategyName + "'; initializeCount="
				+ initializeCount + "]";
	}

}
