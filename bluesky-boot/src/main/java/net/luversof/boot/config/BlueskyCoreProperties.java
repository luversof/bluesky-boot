package net.luversof.boot.config;

/**
 * BlueskyProperties를 확장하여 사용하는 properties 객체 중 유일하게 primary로 설정해야 하는 properties
 * @author bluesky
 *
 * @param <T>
 */
public interface BlueskyCoreProperties<T extends BlueskyCoreModuleProperties> extends BlueskyProperties<T> {
	
	/**
	 * module 호출 처리 방식 정의 <br />
	 * [domain , addPathPattern, moduleNameResolver]
	 */
	CoreModulePropertiesResolveType getResolveType();
	void setResolveType(CoreModulePropertiesResolveType resolveType);
	
	
	public enum CoreModulePropertiesResolveType {

		/**
		 * 도메인 기준 module 분기 처리 시 사용
		 */
		DOMAIN,

		/**
		 * request path 기준으로 분기 처리 시 사용
		 */
		ADD_PATH_PATTERN,

		/**
		 * 별도 resolver를 구현한 경우 사용 <br />
		 * 도메인을 사용하지 않는 API 서버와 같은 경우 추가 구현하여 분기 처리를 할 수 있도록 제공함
		 */
		MODULE_NAME_RESOLVER;
	}

}
