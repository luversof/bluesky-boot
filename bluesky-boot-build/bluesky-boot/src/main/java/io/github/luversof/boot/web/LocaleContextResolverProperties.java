package io.github.luversof.boot.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.github.luversof.boot.context.ApplicationContextUtil;
import io.github.luversof.boot.core.BlueskyProperties;
import io.github.luversof.boot.web.servlet.i18n.LocaleContextResolveHandler;
import io.github.luversof.boot.web.servlet.i18n.handler.AcceptHeaderLocaleContextResolveHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * LocaleContextResovler에서 사용할 localeContextResolveHandler를 설정
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "bluesky-boot.web.locale-context-resolver")
public class LocaleContextResolverProperties implements BlueskyProperties {

	/**
	 * 목록에 대한 preset 제공
	 */
	private LocaleContextResolveHandlerPreset preset;
	
	/**
	 * 사용할 localeResolverHandler beanName 목록, 순서대로 수행됨
	 */
	@Builder.Default
	private List<String> localeContextResolveHandlerBeanNameList = new ArrayList<>();

	@JsonIgnore
	public List<LocaleContextResolveHandler> getLocaleResolveHandlerList() {
		if (localeContextResolveHandlerBeanNameList == null) {
			return Collections.emptyList(); 
		}
		return localeContextResolveHandlerBeanNameList.stream().map(beanName -> ApplicationContextUtil.getApplicationContext().getBean(beanName, LocaleContextResolveHandler.class)).sorted(Comparator.comparingInt(LocaleContextResolveHandler::getOrder)).toList();
	}
	
	/**
	 * preset이 지정되어 있는 경우 별도 설정이 없으면 preset을 localeResolverHandlerBeanNameList에 설정
	 * 모듈별 properties는 그럼 어떻게 동작하나?
	 */
	@Override
	public void load() {
		if (preset == null) {
			return;
		}
		
		if (localeContextResolveHandlerBeanNameList != null &&  !localeContextResolveHandlerBeanNameList.isEmpty()) {
			return;
		}
		
		localeContextResolveHandlerBeanNameList = preset.getLocaleContextResolveHandlerBeanNameList();
	}
	
	@AllArgsConstructor
	@Getter
	public enum LocaleContextResolveHandlerPreset {
		BASIC(List.of("cookieLocaleResolveHandler", AcceptHeaderLocaleContextResolveHandler.DEFAULT_BEAN_NAME)),
		ACCEPT_HEADER(List.of(AcceptHeaderLocaleContextResolveHandler.DEFAULT_BEAN_NAME))
		;
		
		private List<String> localeContextResolveHandlerBeanNameList;
	}
}