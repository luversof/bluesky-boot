package io.github.luversof.boot.core;

import java.util.function.BiConsumer;

import org.springframework.beans.BeanUtils;

/**
 * BlueskyProperties를 구현하는 properties가 properties에 설정된 값과 builder가 제공하는 기본 값을 병합하여 재설정할 수 있도록 지원
  *
 * @param <P>
 * @param <B>
 */
public abstract class AbstractBlueskyProperties<P extends BlueskyProperties, B extends BlueskyPropertiesBuilder<P>> implements BlueskyProperties {

	private static final long serialVersionUID = 1L;

	/**
	 * builer가 제공하는 기본 값과 properties에 설정된 값을 병합한 후 병합된 값을 재설정
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void load() {
		var builder = getBuilder();
		getPropertyMapperConsumer().accept((P) this, builder);
		BeanUtils.copyProperties(builder.build(), this);
	}
	
	/**
	 * properties의 값을 builder로 맵핑하는 consumer
	 * @return
	 */
	protected abstract BiConsumer<P, B> getPropertyMapperConsumer();
	
	/**
	 * Builder instance
	 * @return
	 */
	protected abstract B getBuilder();
	
}
