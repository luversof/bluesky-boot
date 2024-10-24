package io.github.luversof.boot.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "bluesky-boot.core")
public class CoreBaseProperties implements InitializingBean {
	
	/**
	 * Define module invocation criteria
	 * 
	 * [domain (default), addPathPattern, moduleNameResolver]
	 */
	@Builder.Default
	private CoreResolveType resolveType = CoreResolveType.DOMAIN;
	
	/**
	 * 별다른 설정 없이 moduleName만 지정하고자 하는 경우 사용
	 */
	@Builder.Default
	private Set<String> moduleNameSet = new HashSet<>();
	
	/**
	 * Exception log 제외 대상 목록
	 * 로그 확인이 불필요한 에러 항목에 대해 exception log 제외 처리
	 * 목록을 추가하고자 하는 경우 bluesky-boot.core.log-except-exception-additional-list로 설정하면 합산처리 됨.
	 */
	@Builder.Default
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

	@Override
	public void afterPropertiesSet() throws Exception {
		BlueskyBootContextHolder.getContext().getModuleNameSet().addAll(getModuleNameSet());
	}

}
