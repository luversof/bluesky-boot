package net.luversof.boot.autoconfigure.core.context;

import java.util.Map;

import net.luversof.boot.autoconfigure.core.config.BlueskyProperties;

public interface BlueskyContext {
	// String moduleName 을 저장할 것인가? 아니면 해당 moduleName에 대한 module을 저장할 것인가?
	String getModuleName();
	void setModuleName(String moduleName);
	
	// 이렇게 쓰는건 어떨까?
	Map<Class<? extends BlueskyProperties<?>>, BlueskyProperties<?>> getBlueskyPropertiesMap();
	void setBlueskyPropertiesMap(Map<Class<? extends BlueskyProperties<?>>, BlueskyProperties<?>> blueskyPropertiesMap);
}
