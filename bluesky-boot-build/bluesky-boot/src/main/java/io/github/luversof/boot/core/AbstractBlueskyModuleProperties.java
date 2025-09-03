package io.github.luversof.boot.core;

import io.github.luversof.boot.context.BlueskyBootContextHolder;

public abstract class AbstractBlueskyModuleProperties<P extends AbstractBlueskyProperties<P, B>, B extends BlueskyPropertiesBuilder<P>> implements BlueskyModuleProperties<P> {

	private static final long serialVersionUID = 1L;

	@Override
	public void load() {
		parentReload();
		
		BlueskyBootContextHolder.getContext().getModuleNameSet().forEach(moduleName -> {
			
			var builder = getBuilder(moduleName);
			
			if (!getModules().containsKey(moduleName)) {
				getModules().put(moduleName, builder.build());
			}
			
			var propertyMapperConsumer = getParent().getPropertyMapperConsumer();
			propertyMapperConsumer.accept(getParent(), builder);
			propertyMapperConsumer.accept(getGroup(moduleName), builder);
			propertyMapperConsumer.accept(getModules().get(moduleName), builder);
			
			getModules().put(moduleName, builder.build());
		});
		
		// getModules().forEach((key, value) -> value.load());	// 이거 꼭 해야 하나? LocaleContextResolverModuleProperties에 설정했었음. 확인 필요
	}

	protected abstract B getBuilder(String moduleName);
}
