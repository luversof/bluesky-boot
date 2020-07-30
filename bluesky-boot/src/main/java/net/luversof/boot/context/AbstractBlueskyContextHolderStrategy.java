package net.luversof.boot.context;

import org.springframework.util.Assert;

import net.luversof.boot.config.BlueskyCoreProperties;
import net.luversof.boot.util.ApplicationContextUtil;

public abstract class AbstractBlueskyContextHolderStrategy implements BlueskyContextHolderStrategy<BlueskyContext> {
	
	protected abstract ThreadLocal<BlueskyContext> getContextHolder();

	@Override
	public void clearContext() {
		getContextHolder().remove();
	}

	@Override
	public BlueskyContext getContext() {
		var ctx = getContextHolder().get();

		if (ctx == null) {
			ctx = createEmptyContext();
			getContextHolder().set(ctx);
		}

		return ctx;
	}

	@Override
	public void setContext(BlueskyContext context) {
		Assert.notNull(context, "Only non-null BlueskyContext instances are permitted");
		getContextHolder().set(context);
	}

	@Override
	public BlueskyContext createEmptyContext() {
		var ctx = new BlueskyContextImpl();
		BlueskyCoreProperties<?> coreProperties = ApplicationContextUtil.getApplicationContext().getBean(BlueskyCoreProperties.class);
		
		Assert.notEmpty(coreProperties.getModules(), "coreProperties is not set");
		Assert.state(coreProperties.getModules().size() == 1, "For multi module based projects, setContext should be done first");
		
		ctx.setModuleName(coreProperties.getModules().entrySet().stream().findAny().get().getKey());
		return ctx;
	}
	
	@Override
	public boolean hasContext() {
		return getContextHolder().get() != null;
	}
}
