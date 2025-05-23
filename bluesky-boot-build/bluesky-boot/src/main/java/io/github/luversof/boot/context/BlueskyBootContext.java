package io.github.luversof.boot.context;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.github.luversof.boot.core.ModuleInfo;
import lombok.Data;

/**
 * A context object to manage the data that is used while resident
 * BlueskyContext is created per request, while BlueskyBootContext persists while the application is running.
 * 
 */
@Data
public class BlueskyBootContext {
	
	/**
	 * refresh 목적으로 clear하려는 경우 사용
	 */
	public void clear() {
		this.moduleNameSet.clear();
		this.parentModuleInfo = null;
		this.moduleInfoMap.clear();
	}

	/**
	 * moduleName 전체 목록
	 * 해당 목록을 기준으로 각 properties의 module을 생성함 
	 */
	private final Set<String> moduleNameSet = new HashSet<>();
	
	/**
	 * ModuleInfo 값을 사용하는 properties에서 공통 moduleInfo 설정 참조 시 사용 
	 */
	private ModuleInfo parentModuleInfo;
	
	/**
	 * moduleName에 지정된 moduleInfo가 있는 경우 추가되는 map
	 * 해당 값을 기준으로 builder를 통한 preset을 사용한다.
	 */
	private final Map<String, ModuleInfo> moduleInfoMap = new HashMap<>();
	
	/**
	 * 최초 properties 값 저장
	 * refresh 사용 시 초기 진행을 위한 목적
	 * DevTools RestartClassLoader 오류로 인해 BlueskyResfreshProperties가 아닌 Serializable 로 정의
	 * 이후 해결되면 BlueskyResfreshProperties로 변경 예정
	 */
	private final Map<String, Serializable> initialBlueskyResfreshPropertiesMap = new HashMap<>();

}
