package io.github.luversof.boot.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.github.luversof.boot.context.ApplicationContextUtil;
import io.github.luversof.boot.core.BlueskyProperties;
import io.github.luversof.boot.web.servlet.i18n.LocaleResolveHandler;
import io.github.luversof.boot.web.servlet.i18n.handler.AcceptHeaderLocaleResolveHandler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * LocaleContextResovler에서 사용할 localeContextResolveHandler를 설정
 */
@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "bluesky-boot.web.locale-context-resolver")
public class LocaleContextResolverProperties implements BlueskyProperties {
	
	private static final long serialVersionUID = 1L;

	private String beanName;

	/**
	 * 목록에 대한 preset 제공
	 */
	private LocaleContextResolveHandlerPreset preset;
	
	/**
	 * 사용할 localeResolverHandler beanName 목록, 순서대로 수행됨
	 */
	@Builder.Default
	private List<String> localeResolveHandlerBeanNameList = new ArrayList<>();

	@JsonIgnore
	public List<LocaleResolveHandler> getLocaleResolveHandlerList() {
		if (localeResolveHandlerBeanNameList == null) {
			return Collections.emptyList(); 
		}
		
		Map<String, LocaleResolveHandler> beansOfType = ApplicationContextUtil.getApplicationContext().getBeansOfType(LocaleResolveHandler.class);
		
		List<LocaleResolveHandler> localeResolveHandlerList = new ArrayList<>();
		localeResolveHandlerBeanNameList.forEach(beanName -> {
			if (!beansOfType.containsKey(beanName)) {
				log.warn("LocaleResolveHandler bean {} is not exist", beanName);
				return;
			}
			localeResolveHandlerList.add(beansOfType.get(beanName));
		});
		return localeResolveHandlerList;
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
		
		if (localeResolveHandlerBeanNameList != null &&  !localeResolveHandlerBeanNameList.isEmpty()) {
			return;
		}
		
		localeResolveHandlerBeanNameList = preset.getLocaleContextResolveHandlerBeanNameList();
	}
	
	@AllArgsConstructor
	@Getter
	public enum LocaleContextResolveHandlerPreset {
		BASIC(List.of("cookieLocaleResolveHandler", AcceptHeaderLocaleResolveHandler.DEFAULT_BEAN_NAME)),
		ACCEPT_HEADER(List.of(AcceptHeaderLocaleResolveHandler.DEFAULT_BEAN_NAME))
		;
		
		private List<String> localeContextResolveHandlerBeanNameList;
	}
}