package io.github.luversof.boot.core;

import java.util.function.BiConsumer;

import io.github.luversof.boot.context.BlueskyBootContextHolder;

public abstract class AbstractBlueskyGroupProperties<P extends AbstractBlueskyProperties<P, B>, B extends BlueskyPropertiesBuilder<P>> implements BlueskyGroupProperties<P> {
	
	private static final long serialVersionUID = 1L;

	@Override
	public void load() {
		parentReload();
		
		BlueskyBootContextHolder.getContext().getGroupModules().keySet().forEach(groupName -> {
			var builder = getBuilder(groupName);
			
			// group이 없는 경우 기본 설정 추가
			if (!getGroups().containsKey(groupName)) {
				getGroups().put(groupName, builder.build());
			}
			
			BiConsumer<P, B> propertyMapperConsumer = getParent().getPropertyMapperConsumer();
			propertyMapperConsumer.accept(getParent(), builder);
			propertyMapperConsumer.accept(getGroups().get(groupName), builder);
			
			getGroups().put(groupName, builder.build());
		});
	}
	
	protected abstract B getBuilder(String groupName);
}
