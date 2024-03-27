package io.github.luversof.boot.context;

import org.springframework.util.Assert;

import io.github.luversof.boot.core.BlueskyCoreProperties;
import io.github.luversof.boot.util.ApplicationContextUtil;

final class ThreadLocalBlueskyContextHolderStrategy implements BlueskyContextHolderStrategy {

	private final ThreadLocal<BlueskyContext> contextHolder = new ThreadLocal<>();

	@Override
	public void clearContext() {
		contextHolder.remove();
	}

	@Override
	public BlueskyContext getContext() {
		var context = contextHolder.get();
		if (context == null) {
			context = createEmptyContext();
			contextHolder.set(context);
		}
		return context;
	}

	@Override
	public void setContext(BlueskyContext context) {
		Assert.notNull(context, "Only non-null BlueskyContext instances are permitted");
		contextHolder.set(context);
	}

	@Override
	public BlueskyContext createEmptyContext() {
		BlueskyCoreProperties<?> coreProperties = ApplicationContextUtil.getApplicationContext().getBean(BlueskyCoreProperties.class);
		
		Assert.notEmpty(coreProperties.getModules(), "coreProperties is not set");
		Assert.state(coreProperties.getModules().size() == 1, "For multi module based projects, setContext should be done first");
		var module = coreProperties.getModules().entrySet().stream().findAny().orElse(null);
		Assert.state(module != null, "module configuration is required");
		return module::getKey;
	}

}
