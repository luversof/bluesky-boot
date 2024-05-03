package io.github.luversof.boot.core;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

public interface BlueskyProperties extends InitializingBean {

	@Override
	default void afterPropertiesSet() throws Exception {
		
	}

	/**
	 * 의존성 지정을 위해 선언
	 * @param coreProperties
	 */
	@Autowired
	default void setCoreProperties(CoreProperties coreProperties) {
		
	}
}