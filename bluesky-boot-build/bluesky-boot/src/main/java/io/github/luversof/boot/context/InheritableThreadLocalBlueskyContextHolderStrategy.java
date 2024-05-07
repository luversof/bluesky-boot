package io.github.luversof.boot.context;

import org.springframework.util.Assert;

import io.github.luversof.boot.core.CoreModuleProperties;
import io.github.luversof.boot.util.ApplicationContextUtil;

final class InheritableThreadLocalBlueskyContextHolderStrategy implements BlueskyContextHolderStrategy {

	private final ThreadLocal<BlueskyContext> contextHolder = new InheritableThreadLocal<>();

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
		CoreModuleProperties coreModuleProperties = ApplicationContextUtil.getApplicationContext().getBean(CoreModuleProperties.class);
		
		if (coreModuleProperties.getModules().isEmpty()) {
			return () -> null;
		} else if (coreModuleProperties.getModules().size() == 1) {
			return () -> coreModuleProperties.getModules().keySet().stream().findAny().orElse(null);
		}
		
//		Assert.notEmpty(coreModuleProperties.getModules(), "coreProperties is not set");
//		Assert.state(coreModuleProperties.getModules().size() == 1, "For multi module based projects, setContext should be done first");
		Assert.state(coreModuleProperties.getModules().size() > 1, "When using multi module, it is necessary to specify the target module.");
		return null;
	}

}
