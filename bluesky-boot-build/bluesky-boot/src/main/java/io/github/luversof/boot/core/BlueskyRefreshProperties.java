package io.github.luversof.boot.core;

import java.io.Serializable;

import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.util.SerializationUtils;

import io.github.luversof.boot.context.ApplicationContextUtil;
import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.exception.BlueskyException;

/**
 * Provides refresh/reset functions for refreshScope target properties
 */
@RefreshScope
public interface BlueskyRefreshProperties extends Serializable {
	
	/**
	 * If you use multiple beans in the same class, you need to specify the beanName through beanNameAware implementation.
	 * @return
	 */
	default String getBeanName() {
		return null;
	}

	/**
	 * Save the values ​​of the first properties
	 */
	default void initialRefreshPropertiesStore() {
		
		String beanName = getBeanName();
		
		if (beanName == null) {
			String[] beanNamesForType = ApplicationContextUtil.getApplicationContext().getBeanNamesForType(this.getClass());
			if (beanNamesForType.length > 1) {
				throw new BlueskyException("properties beanName must be set");
			}
			beanName = beanNamesForType[0];
		}
		
		var initialBlueskyResfreshPropertiesMap = BlueskyBootContextHolder.getContext().getInitialBlueskyResfreshPropertiesMap();
		initialBlueskyResfreshPropertiesMap.computeIfAbsent(beanName, key -> SerializationUtils.clone(this));
		
	}
	
	/**
	 * Save the initially loaded properties values
	 */
	default void initialLoadRefreshPropertiesStore() {
		
		String beanName = getBeanName();
		
		if (beanName == null) {
			String[] beanNamesForType = ApplicationContextUtil.getApplicationContext().getBeanNamesForType(this.getClass());
			if (beanNamesForType.length > 1) {
				throw new BlueskyException("properties beanName must be set");
			}
			beanName = beanNamesForType[0];
		}
		
		var initialLoadBlueskyResfreshPropertiesMap = BlueskyBootContextHolder.getContext().getInitialLoadBlueskyResfreshPropertiesMap();
		initialLoadBlueskyResfreshPropertiesMap.computeIfAbsent(beanName, key -> SerializationUtils.clone(this));
	}
	
}
