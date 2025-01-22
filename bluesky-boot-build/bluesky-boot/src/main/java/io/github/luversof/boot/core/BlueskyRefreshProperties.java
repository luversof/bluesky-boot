package io.github.luversof.boot.core;

import java.io.Serializable;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.SerializationUtils;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.github.luversof.boot.context.ApplicationContextUtil;
import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.exception.BlueskyException;

/**
 * Provides refresh/reset functions for refreshScope target properties
 */
public interface BlueskyRefreshProperties extends Serializable, InitializingBean {
	
	/**
	 * If you use multiple beans in the same class, you need to specify the beanName through beanNameAware implementation.
	 * @return
	 */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	default String getBeanName() {
		return null;
	}

	/**
	 * Save the values ​​of the first properties
	 */
	default void initialRefreshPropertiesStore() {
		
		String beanName = getBeanName();
		
		if (beanName == null) {
			String[] beanNames = ApplicationContextUtil.getApplicationContext().getBeanNamesForType(this.getClass());
			if (beanNames.length > 1) {
				throw new BlueskyException("properties beanName must be set");
			}
			beanName = beanNames[0];
		}
		
		BlueskyBootContextHolder.getContext().getInitialBlueskyResfreshPropertiesMap()
			.computeIfAbsent(beanName, key -> SerializationUtils.clone(this));
		
	}
	
	/**
	 * Save the initially loaded properties values
	 */
	default void initialLoadRefreshPropertiesStore() {
		
		String beanName = getBeanName();
		
		if (beanName == null) {
			String[] beanNames = ApplicationContextUtil.getApplicationContext().getBeanNamesForType(this.getClass());
			if (beanNames.length > 1) {
				throw new BlueskyException("properties beanName must be set");
			}
			beanName = beanNames[0];
		}
		
		BlueskyBootContextHolder.getContext().getInitialLoadBlueskyResfreshPropertiesMap()
			.computeIfAbsent(beanName, key -> SerializationUtils.clone(this));
	}
	
}
