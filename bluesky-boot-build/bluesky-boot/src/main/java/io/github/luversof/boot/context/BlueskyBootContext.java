package io.github.luversof.boot.context;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.github.luversof.boot.core.ModuleInfo;
import lombok.Data;

/**
 * 이거 상위 설정과 module 설정에 대해 정리가 좀 필요할듯?
 * 
 */
@Data
public class BlueskyBootContext {

	/**
	 * moduleName 전체 목록
	 * 해당 목록을 기준으로 module을 생성함 
	 */
	private final Set<String> moduleNameSet = new HashSet<>();
	
	/**
	 * coreProperties에 등록된 moduleInfo 설정
	 */
	private ModuleInfo parentModuleInfo;
	
	/**
	 * modneName에 지정된 moduleInfo가 있는 경우 추가되는 map 
	 */
	private final Map<String, ModuleInfo> moduleInfoMap = new HashMap<>();
	
	
	

}
