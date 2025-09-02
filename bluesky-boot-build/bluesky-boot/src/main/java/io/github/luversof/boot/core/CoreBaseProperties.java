package io.github.luversof.boot.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = CoreBaseProperties.PREFIX)
public class CoreBaseProperties implements BlueskyProperties {
	
	private static final long serialVersionUID = 1L;
	
	public static final String PREFIX = "bluesky-boot.core";
	
	/**
	 * Bean 생성 시 지정할 이름
	 */
	public static final String BEAN_NAME = "blueskyCoreBaseProperties";

	/**
	 * Define module invocation criteria
	 * 
	 * [domain (default), addPathPattern, moduleNameResolver]
	 */
	private CoreResolveType resolveType = CoreResolveType.DOMAIN;
	
	/**
	 * 별다른 설정 없이 moduleName만 지정하고자 하는 경우 사용
	 * CoreProperties의 module 설정과 이 moduleNameSet 설정을 병합하여 moduleNameSet을 관리함
	 */
	private Set<String> moduleNameSet = new HashSet<>();
	
	/**
	 * 여러 모듈이 반복된 설정을 사용하는 경우 BlueskyGroupProperties를 설정하고 이 groupModules 로 각 module이 속한 group을 지정하여 group properties를 module properties로 전파 처리함
	 */
	private Map<String, List<String>> groupModules = new HashMap<>();
	
	/**
	 * Exception log 제외 대상 목록
	 * 로그 확인이 불필요한 에러 항목에 대해 exception log 제외 처리
	 * 목록을 추가하고자 하는 경우 bluesky-boot.core.log-except-exception-additional-list로 설정하면 합산처리 됨.
	 */
	private List<String> logExceptExceptionList = new ArrayList<>();
	
	public void setLogExceptExceptionList(List<String> list) {
		for (String value : list) {
			if (!this.logExceptExceptionList.contains(value)) {
				this.logExceptExceptionList.add(value);
			}
		}
	}
	
	/**
	 * logExceptExceptionList에 추가하고자 하는 경우 사용
	 */
	public void setLogExceptExceptionAdditionalList(String... values) {
		setLogExceptExceptionList(Arrays.asList(values));
	}

	public String[] getLogExceptExceptionAdditionalList() {
		return new String[] {};
	}

	/**
	 * BlueskyProperties 중 제일 먼저 호출되기 때문에 refresh 초기화 관련 처리를 여기에서 담당함
	 */
	@Override
	public void load() {
		var blueskyBootContext = BlueskyBootContextHolder.getContext();
		blueskyBootContext.clear();
		blueskyBootContext.getModuleNameSet().addAll(getModuleNameSet());
		blueskyBootContext.getGroupModules().putAll(getGroupModules());
	}
	
	public static CoreBasePropertiesBuilder builder() {
		return new CoreBasePropertiesBuilder();
	}
	
	@NoArgsConstructor(access = AccessLevel.NONE)
	public static class CoreBasePropertiesBuilder implements BlueskyPropertiesBuilder<CoreBaseProperties> {
		
		private CoreResolveType resolveType = CoreResolveType.DOMAIN;
		
		private Set<String> moduleNameSet = new HashSet<>();
		
		private Map<String, List<String>> groupModules = new HashMap<>();
		
		private List<String> logExceptExceptionList = new ArrayList<>();
		
		public CoreBasePropertiesBuilder resolveType(CoreResolveType resolveType) {
			this.resolveType = resolveType;
			return this;
		}
		
		public CoreBasePropertiesBuilder moduleNameSet(Set<String> moduleNameSet) {
			this.moduleNameSet = moduleNameSet;
			return this;
		}
		
		public CoreBasePropertiesBuilder groupModules(Map<String, List<String>> groupModules) {
			this.groupModules = groupModules;
			return this;
		}
		
		public CoreBasePropertiesBuilder logExceptExceptionList(List<String> logExceptExceptionList) {
			this.logExceptExceptionList = logExceptExceptionList;
			return this;
		}
		
		public CoreBaseProperties build() {
			return new CoreBaseProperties(
				this.resolveType == null ? CoreResolveType.DOMAIN : this.resolveType,
				this.moduleNameSet == null ? new HashSet<>() : this.moduleNameSet,
				this.groupModules == null ? new HashMap<>() : this.groupModules,
				this.logExceptExceptionList == null ? new ArrayList<>() : this.logExceptExceptionList
			);
		}
	}

}
