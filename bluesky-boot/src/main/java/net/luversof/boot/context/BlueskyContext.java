package net.luversof.boot.context;

import net.luversof.boot.config.BlueskyCoreModuleProperties;
import net.luversof.boot.config.BlueskyProperties;

public interface BlueskyContext {
	
	String getModuleName();
	void setModuleName(String moduleName);
	
	/**
	 * 설정된 moduleName을 기반으로 호출되는 정보
	 * @return
	 */
	<T, U extends BlueskyProperties<T>> T getModule(Class<U> u);
	
	/**
	 * coreModule의 경우 가장 자주 쓰이기 때문에 기본 제공
	 * @return
	 */
	<T extends BlueskyCoreModuleProperties> T getCoreModule();
}
