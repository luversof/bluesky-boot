package io.github.luversof.boot.web.servlet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.github.luversof.boot.context.ApplicationContextUtil;
import io.github.luversof.boot.core.BlueskyProperties;
import io.github.luversof.boot.web.servlet.i18n.LocaleResolveHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * LocaleContextResolver 관련 설정 담당
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "bluesky-boot.web.servlet.locale-context-resolver")
public class LocaleContextResolverProperties implements BlueskyProperties {

	/**
	 * 목록에 대한 preset 제공
	 */
	private LocaleContextResolverPreSet preset;
	
	/**
	 * 사용할 localeResolverHandler beanName 목록, 순서대로 수행됨
	 */
	@Builder.Default
	private List<String> localeResolverHandlerBeanNameList = new ArrayList<>();

	@JsonIgnore
	public List<LocaleResolveHandler> getLocaleResolveHandlerList() {
		if (localeResolverHandlerBeanNameList == null) {
			return Collections.emptyList(); 
		}
		return localeResolverHandlerBeanNameList.stream().map(beanName -> ApplicationContextUtil.getApplicationContext().getBean(beanName, LocaleResolveHandler.class)).sorted(Comparator.comparingInt(LocaleResolveHandler::getOrder)).toList();
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
		
		if (localeResolverHandlerBeanNameList != null &&  !localeResolverHandlerBeanNameList.isEmpty()) {
			return;
		}
		
		localeResolverHandlerBeanNameList = preset.getLocaleResolverHandlerBeanNameList();
	}
	
	@AllArgsConstructor
	@Getter
	public enum LocaleContextResolverPreSet {
		BASIC(List.of(""))
		
		;
		
		private List<String> localeResolverHandlerBeanNameList;
	}
}