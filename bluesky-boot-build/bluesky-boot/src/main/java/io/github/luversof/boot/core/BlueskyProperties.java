package io.github.luversof.boot.core;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;

@RefreshScope
public interface BlueskyProperties extends InitializingBean {
	
	default void load() {}
	
	@Override
	default void afterPropertiesSet() throws Exception {
		load();
	}

	/**
	 * 의존성 지정을 위해 선언
	 * Properties Bean load 시 항상 CoreProperties가 우선 생성됨
	 * @param coreProperties
	 */
	@Autowired
	default void setCoreProperties(CoreProperties coreProperties) {
	}

}