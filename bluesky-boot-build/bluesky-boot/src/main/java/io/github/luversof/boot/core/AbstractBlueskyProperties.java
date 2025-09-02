package io.github.luversof.boot.core;

import java.util.function.BiConsumer;

import org.springframework.beans.BeanUtils;

public abstract class AbstractBlueskyProperties<P extends BlueskyProperties, B extends BlueskyPropertiesBuilder<P>> implements BlueskyProperties {

	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unchecked")
	@Override
	public void load() {
		var builder = getBuilder();
		getPropertyMapperConsumer().accept((P) this, builder);
		BeanUtils.copyProperties(builder.build(), this);
	}
	
	protected abstract BiConsumer<P, B> getPropertyMapperConsumer();
	
	protected abstract B getBuilder();
	
}
