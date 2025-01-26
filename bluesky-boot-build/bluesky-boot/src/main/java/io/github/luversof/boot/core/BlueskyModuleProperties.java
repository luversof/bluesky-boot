package io.github.luversof.boot.core;

import java.util.Map;

import org.springframework.beans.factory.InitializingBean;

/**
 * Top-level class provided for handling module branching
 * 
 * Used to implement per-feature module branch handling
 * @author bluesky
 *
 * @param <T> ModuleProperties가 module에 담을 대상 BlueskyProperties 타입
 */
public interface BlueskyModuleProperties<T extends BlueskyProperties> extends InitializingBean, BlueskyRefreshProperties {
	
	T getParent();
	
	Map<String, T> getModules();
	
	default void load() {}
	
	@Override
	default void afterPropertiesSet() throws Exception {
		storeInitialProperties();
		load();
	}

}
