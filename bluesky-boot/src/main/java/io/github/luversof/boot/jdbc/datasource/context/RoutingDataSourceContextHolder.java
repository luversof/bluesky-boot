package io.github.luversof.boot.jdbc.datasource.context;

import org.springframework.util.Assert;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RoutingDataSourceContextHolder {

	private static final ThreadLocal<RoutingDataSourceContext> contextHolder = new ThreadLocal<>();

	public static void clearContext() {
		contextHolder.remove();
	}

	public static RoutingDataSourceContext getContext() {
		var context = contextHolder.get();
		if (context == null) {
			context = () -> null;
			contextHolder.set(context);
		}
		return context;
	}

	public static void setContext(RoutingDataSourceContext context) {
		Assert.notNull(context, "Only non-null RoutingDataSourceContext instances are permitted");
		contextHolder.set(context);
	}

}
