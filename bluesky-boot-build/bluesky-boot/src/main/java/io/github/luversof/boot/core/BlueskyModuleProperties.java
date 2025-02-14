package io.github.luversof.boot.core;

import java.util.Map;

import org.springframework.beans.factory.InitializingBean;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.github.luversof.boot.context.ApplicationContextUtil;

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
	void setParent(T parent);
	
	Map<String, T> getModules();
	
	default void load() {
		parentReload();
	}
	
	@Override
	default void afterPropertiesSet() throws Exception {
		storeInitialProperties();
		load();
	}

	/**
	 * parent의 beanName을 기준으로 parent 조회
	 * actuator refresh 한 경우 참조된 parent가 갱신되지 않으므로 제공 
	 * @return
	 */
	@JsonIgnore
	@SuppressWarnings("unchecked")
	default void parentReload() {
		var applicationContext =  ApplicationContextUtil.getApplicationContext();
		var parentBeanName = getParent().getBeanName();
		if (parentBeanName == null) {
			parentBeanName = applicationContext.getBeanNamesForType(this.getParent().getClass())[0];
		}
		setParent((T) applicationContext.getBean(parentBeanName));
	}
}
