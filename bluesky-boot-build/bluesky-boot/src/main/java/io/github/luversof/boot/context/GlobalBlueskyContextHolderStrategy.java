package io.github.luversof.boot.context;

import org.springframework.util.Assert;

import io.github.luversof.boot.core.CoreModuleProperties;
import io.github.luversof.boot.util.ApplicationContextUtil;

final class GlobalBlueskyContextHolderStrategy implements BlueskyContextHolderStrategy {

	private BlueskyContext contextHolder;

	@Override
	public void clearContext() {
		contextHolder = null;
	}

	@Override
	public BlueskyContext getContext() {
		if (contextHolder == null) {
			contextHolder = createEmptyContext();
		}

		return contextHolder;
	}

	@Override
	public void setContext(BlueskyContext context) {
		Assert.notNull(context, "Only non-null BlueskyContext instances are permitted");
		contextHolder = context;
	}

	@Override
	public BlueskyContext createEmptyContext() {
		CoreModuleProperties coreModuleProperties = ApplicationContextUtil.getApplicationContext().getBean(CoreModuleProperties.class);
		
		Assert.notEmpty(coreModuleProperties.getModules(), "coreProperties is not set");
		Assert.state(coreModuleProperties.getModules().size() == 1, "For multi module based projects, setContext should be done first");
		var module = coreModuleProperties.getModules().entrySet().stream().findAny().orElse(null);
		Assert.state(module != null, "module configuration is required");
		return module::getKey;
	}

}
