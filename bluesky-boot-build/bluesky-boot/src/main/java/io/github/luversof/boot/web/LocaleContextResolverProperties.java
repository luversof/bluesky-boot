package io.github.luversof.boot.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.github.luversof.boot.context.ApplicationContextUtil;
import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.AbstractBlueskyProperties;
import io.github.luversof.boot.core.BlueskyPropertiesBuilder;
import io.github.luversof.boot.web.servlet.i18n.LocaleResolveHandler;
import io.github.luversof.boot.web.servlet.i18n.handler.AcceptHeaderLocaleResolveHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * LocaleContextResovler에서 사용할 localeContextResolveHandler를 설정
 */
@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = LocaleContextResolverProperties.PREFIX)
public class LocaleContextResolverProperties extends AbstractBlueskyProperties<LocaleContextResolverProperties, LocaleContextResolverProperties.LocaleContextResolverPropertiesBuilder> {
	
	private static final long serialVersionUID = 1L;
	
	public static final String PREFIX = "bluesky-boot.web.locale-context-resolver";

	/**
	 * 목록에 대한 preset 제공
	 */
	private LocaleContextResolveHandlerPreset preset;
	
	/**
	 * 사용할 localeResolverHandler beanName 목록, 순서대로 수행됨
	 */
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
	
	@AllArgsConstructor
	@Getter
	public enum LocaleContextResolveHandlerPreset {
		BASIC(List.of("cookieLocaleResolveHandler", AcceptHeaderLocaleResolveHandler.DEFAULT_BEAN_NAME)),
		ACCEPT_HEADER(List.of(AcceptHeaderLocaleResolveHandler.DEFAULT_BEAN_NAME))
		;
		
		private List<String> localeContextResolveHandlerBeanNameList;
	}

	/**
	 * preset이 지정되어 있는 경우 별도 설정이 없으면 preset을 localeResolverHandlerBeanNameList에 설정
	 * 모듈별 properties는 그럼 어떻게 동작하나?
	 */
	protected BiConsumer<LocaleContextResolverProperties, LocaleContextResolverPropertiesBuilder> getPropertyMapperConsumer() {
		return (properties, builder) -> {
			if (properties == null) {
				return;
			}
			var propertyMapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
			propertyMapper.from(properties::getPreset).to(builder::preset);
			if (properties.getLocaleResolveHandlerBeanNameList() != null && !localeResolveHandlerBeanNameList.isEmpty()) {
				propertyMapper.from(properties::getLocaleResolveHandlerBeanNameList).to(builder::localeResolveHandlerBeanNameList);
			} else if (properties.preset != null) {
				propertyMapper.from(properties.getPreset().getLocaleContextResolveHandlerBeanNameList()).when(x -> x == null || x.isEmpty()).to(builder::localeResolveHandlerBeanNameList);
			}
		};
	}
	
	@Override
	protected LocaleContextResolverPropertiesBuilder getBuilder() {
		var blueskyBootContext = BlueskyBootContextHolder.getContext();
		var parentModuleInfo = blueskyBootContext.getParentModuleInfo();
		return parentModuleInfo == null ? LocaleContextResolverProperties.builder() : parentModuleInfo.getLocaleContextResolverPropertiesBuilder();
	}
	
	
	public static LocaleContextResolverPropertiesBuilder builder() {
		return new LocaleContextResolverPropertiesBuilder();
	}
	
	public static class LocaleContextResolverPropertiesBuilder implements BlueskyPropertiesBuilder<LocaleContextResolverProperties> {
		
		private LocaleContextResolveHandlerPreset preset;
		
		private List<String> localeResolveHandlerBeanNameList = new ArrayList<>();
		
		private LocaleContextResolverPropertiesBuilder() {
		}

		public LocaleContextResolverPropertiesBuilder preset(LocaleContextResolveHandlerPreset preset) {
			this.preset = preset;
			return this;
		}

		public LocaleContextResolverPropertiesBuilder localeResolveHandlerBeanNameList(List<String> localeResolveHandlerBeanNameList) {
			this.localeResolveHandlerBeanNameList = localeResolveHandlerBeanNameList;
			return this;
		}

		@Override
		public LocaleContextResolverProperties build() {
			return new LocaleContextResolverProperties(preset, localeResolveHandlerBeanNameList);
		}
	}
}