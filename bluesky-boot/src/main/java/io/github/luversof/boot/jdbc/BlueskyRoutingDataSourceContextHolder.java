package io.github.luversof.boot.jdbc;

import org.springframework.util.Assert;

public class BlueskyRoutingDataSourceContextHolder {

	private static final ThreadLocal<BlueskyRoutingDataSourceContext> contextHolder = new ThreadLocal<>();

	public static void clearContext() {
		contextHolder.remove();
	}

	public static BlueskyRoutingDataSourceContext getContext() {
		var context = contextHolder.get();
		if (context == null) {
			context = createEmptyContext();
			contextHolder.set(context);
		}
		return context;
	}

	public static void setContext(BlueskyRoutingDataSourceContext context) {
		Assert.notNull(context, "Only non-null BlueskyContext instances are permitted");
		contextHolder.set(context);
	}

	public static BlueskyRoutingDataSourceContext createEmptyContext() {
		// TODO Auto-generated method stub
		return null;
	}

}
