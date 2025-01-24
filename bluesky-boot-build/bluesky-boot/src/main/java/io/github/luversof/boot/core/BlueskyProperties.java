package io.github.luversof.boot.core;

import org.springframework.beans.factory.InitializingBean;

/**
 * 모듈 기반 설정을 제공하기 위한 interface
 * 모든 BlueskyProperties 중 CoreBaseProperties, CoreProperties가 가장 우선 순위가 높음
 */
public interface BlueskyProperties extends InitializingBean, BlueskyRefreshProperties {
	
	default void load() {}
	
	@Override
	default void afterPropertiesSet() throws Exception {
		storeInitialProperties();
		load();
		storeInitialLoadedProperties();
	}

}