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

	// TODO 목록에 대한 preset 제공
	
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
	
}