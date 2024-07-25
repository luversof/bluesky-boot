package io.github.luversof.boot.web;

import java.util.List;

import lombok.Data;

/**
 * LocaleContextResolver 관련 설정 담당
 */
@Data
public class LocaleContextResolverProperties {

	// 사용할 localeResolver 목록 설정
	// TODO 목록에 대한 preset 제공
	
	private List<String> localeResolverHandlerList;
	
}