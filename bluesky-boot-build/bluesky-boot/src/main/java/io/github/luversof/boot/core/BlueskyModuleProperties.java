package io.github.luversof.boot.core;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.ResolvableType;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.github.luversof.boot.context.ApplicationContextUtil;
import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.context.BlueskyContextHolder;

/**
 * Top-level class provided for handling module branching
 * 
 * Used to implement per-feature module branch handling
 * @author bluesky
 *
 * @param <T> ModuleProperties가 module에 담을 대상 BlueskyProperties 타입
 */
public interface BlueskyModuleProperties<T extends BlueskyProperties> extends InitializingBean, BlueskyRefreshProperties {
	
	Logger LOGGER = LoggerFactory.getLogger(BlueskyModuleProperties.class);
	
	T getParent();
	void setParent(T parent);
	
	Map<String, T> getModules();
	
	default String getGroupPropertiesBeanName() {
		return null;
	}
	
	@SuppressWarnings("unchecked")
	default T getGroup(String moduleName) {
		ResolvableType resolvableType = ResolvableType.forClass(this.getClass()).as(BlueskyModuleProperties.class);
		var blueskyGroupProperties = BlueskyContextHolder.getGroupProperties((Class<T>) resolvableType.getGeneric(0).resolve(), getGroupPropertiesBeanName());
		if (blueskyGroupProperties == null) {
			return null;
		}
		
		List<String> groupNameList = BlueskyBootContextHolder.getContext().getModuleGroups().entrySet().stream()
			.filter(entry -> entry.getValue().contains(moduleName))
			.map(Map.Entry::getKey)
			.toList();
		
		if (groupNameList.isEmpty()) {
			return null;
		}
		
		if (groupNameList.size() > 1) {
			LOGGER.debug("Module names are declared multiple times in multiple groups. moduleName : {}", moduleName);
		}
		
		return blueskyGroupProperties.getGroups().get(groupNameList.get(0));
	}
	
	default void load() {
		parentReload();
	}
	
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
