package net.luversof.boot.autoconfigure.core.context;

import org.springframework.util.Assert;

import net.luversof.boot.autoconfigure.core.config.CoreProperties;
import net.luversof.boot.autoconfigure.core.util.ApplicationContextUtil;

public class ThreadLocalBlueskyContextHolderStrategy implements BlueskyContextHolderStrategy {

	private static final ThreadLocal<BlueskyContext> contextHolder = new ThreadLocal<>();

	public void clearContext() {
		contextHolder.remove();
	}

	public BlueskyContext getContext() {
		BlueskyContext ctx = contextHolder.get();

		if (ctx == null) {
			ctx = createEmptyContext();
			contextHolder.set(ctx);
		}

		return ctx;
	}

	public void setContext(BlueskyContext context) {
		Assert.notNull(context, "Only non-null BrickContext instances are permitted");
		contextHolder.set(context);
	}

	public BlueskyContext createEmptyContext() {
		BlueskyContext ctx = new BlueskyContextImpl();
		CoreProperties  coreProperties = ApplicationContextUtil.getApplicationContext().getBean(CoreProperties.class);
		
		Assert.notEmpty(coreProperties.getModules(), "coreProperties is not set");
		Assert.state(coreProperties.getModules().size() == 1, "For multi module based projects, setContext should be done first");
		
		ctx.setModuleName(coreProperties.getModules().entrySet().stream().findAny().get().getKey());
		return ctx;
	}

}
