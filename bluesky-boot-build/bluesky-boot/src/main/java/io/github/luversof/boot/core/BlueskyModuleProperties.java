package io.github.luversof.boot.core;

import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;

/**
 * Top-level class provided for handling module branching
 * 
 * Used to implement per-feature module branch handling
 * @author bluesky
 *
 * @param <T>
 */
@RefreshScope
public interface BlueskyModuleProperties<T extends BlueskyProperties> extends InitializingBean {
	
	T getParent();
	
	Map<String, T> getModules();
	
	default void load() {}
	
	@Override
	default void afterPropertiesSet() throws Exception {
		load();
	}
	
	/**
	 * 의존성 지정을 위해 선언
	 * @param coreModuleProperties
	 */
	@Autowired
	default void setCoreModuleProperties(CoreModuleProperties coreModuleProperties) {

	}

}
