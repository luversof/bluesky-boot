package io.github.luversof.boot.core;

import java.io.Serializable;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.util.SerializationUtils;

import io.github.luversof.boot.context.BlueskyBootContextHolder;

/**
 * refreshScope 대상 properties 에 대해 refresh / reset 기능 제공
 */
@RefreshScope
public interface BlueskyRefreshProperties extends BeanNameAware, Serializable {
	
	String getBeanName();

	/**
	 * 최초 properties의 값을 저장
	 */
	default void initialRefreshPropertiesStore() {
		var initialBlueskyResfreshPropertiesMap = BlueskyBootContextHolder.getContext().getInitialBlueskyResfreshPropertiesMap();
		if (!initialBlueskyResfreshPropertiesMap.containsKey(getBeanName())) {
			initialBlueskyResfreshPropertiesMap.put(getBeanName(), SerializationUtils.clone(this));
		}
		
	}
	
	/**
	 * 최초 load된 properties 값을 저장
	 */
	default void initialLoadRefreshPropertiesStore() {
		var initialLoadBlueskyResfreshPropertiesMap = BlueskyBootContextHolder.getContext().getInitialLoadBlueskyResfreshPropertiesMap();
		if (!initialLoadBlueskyResfreshPropertiesMap.containsKey(getBeanName())) {
			initialLoadBlueskyResfreshPropertiesMap.put(getBeanName(), SerializationUtils.clone(this));
		}
	}
	
}
