package io.github.luversof.boot.context.i18n;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.BlueskyProperties;
import io.github.luversof.boot.util.function.SerializableSupplier;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

/**
 * 기본적인 Locale 관련 설정을 관리
 */
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = LocaleProperties.PREFIX)
public class LocaleProperties implements BlueskyProperties, BeanNameAware {
	
	private static final long serialVersionUID = 1L;
	
	public static final String PREFIX = "bluesky-boot.locale";
	
	public static final String DEFAULT_BEAN_NAME = "bluesky-boot.locale-io.github.luversof.boot.context.i18n.LocaleProperties";
	public static final String EXTERNAL_LOCALE_BEAN_NAME = "bluesky-boot.external-locale-io.github.luversof.boot.context.i18n.ExternalLocaleProperties";
	
	private String beanName;

	/**
	 * 사용 가능 로케일 목록,
	 * 배열의 첫번째 로케일이 defaultLocale로 처리되며 만약 설정하지 않은 경우 Locale.default()를 사용
	 */
	private List<Locale> enableLocaleList = new ArrayList<>();
	
	public Locale getDefaultLocale() {
		if (enableLocaleList == null || enableLocaleList.isEmpty()) {
			return Locale.getDefault();
		}
		return enableLocaleList.get(0);
	}
	
	protected SerializableSupplier<LocaleProperties.LocalePropertiesBuilder> getBuilderSupplier() {
		return () -> {
			var parentModuleInfo = BlueskyBootContextHolder.getContext().getParentModuleInfo();
			return parentModuleInfo == null ? LocaleProperties.builder() : parentModuleInfo.getLocalePropertiesBuilder();
		};
	}
	
	protected BiConsumer<LocaleProperties, LocalePropertiesBuilder> getPropertyMapperConsumer() {
		return (properties, builder) -> {
			if (properties == null) {
				return;
			}
			var propertyMapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
			propertyMapper.from(properties::getBeanName).to(builder::beanName);
			propertyMapper.from(properties::getEnableLocaleList).whenNot(x -> x == null || x.isEmpty()).to(builder::enableLocaleList);
		};
	}
	
	@Override
	public void load() {
		var builder = getBuilderSupplier().get();
		
		getPropertyMapperConsumer().accept(this, builder);
		
		BeanUtils.copyProperties(builder.build(), this);
	}
	

	public static LocalePropertiesBuilder builder() {
		return new LocalePropertiesBuilder();
	}
	
	@NoArgsConstructor(access = AccessLevel.NONE)
	public static class LocalePropertiesBuilder {
		
		private String beanName;
		
		private List<Locale> enableLocaleList = new ArrayList<>();
		
		public LocalePropertiesBuilder beanName(String beanName) {
			this.beanName = beanName;
			return this;
		}
		
		public LocalePropertiesBuilder enableLocaleList(List<Locale> enableLocaleList) {
			this.enableLocaleList = enableLocaleList;
			return this;
		}
		
		public LocaleProperties build() {
			return new LocaleProperties(
				this.beanName,
				this.enableLocaleList == null ? new ArrayList<>() : this.enableLocaleList
			);
		}
	}
	
}
