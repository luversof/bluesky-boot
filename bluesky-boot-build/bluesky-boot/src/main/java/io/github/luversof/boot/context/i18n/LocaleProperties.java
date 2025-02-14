package io.github.luversof.boot.context.i18n;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;

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
@ConfigurationProperties(prefix = "bluesky-boot.locale")
public class LocaleProperties implements BlueskyProperties, BeanNameAware {
	
	private static final long serialVersionUID = 1L;
	
	public static final String DEFAULT_BEAN_NAME = "localeProperties";
	public static final String EXTERNAL_LOCALE_BEAN_NAME = "externalLocaleProperties";
	
	private final SerializableSupplier<LocaleProperties.LocalePropertiesBuilder> builderSupplier;
	
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
	
	@Override
	public void load() {
		var propertyMapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
		
		var builder = builderSupplier.get();
		
		propertyMapper.from(this::getBuilderSupplier).to(builder::builderSupplier);
		propertyMapper.from(this::getBeanName).to(builder::beanName);
		propertyMapper.from(this::getEnableLocaleList).whenNot(x -> x == null || x.isEmpty()).to(builder::enableLocaleList);
		
		BeanUtils.copyProperties(builder.build(), this);
	}
	

	public static LocalePropertiesBuilder builder() {
		return new LocalePropertiesBuilder();
	}
	
	@NoArgsConstructor(access = AccessLevel.NONE)
	public static class LocalePropertiesBuilder {
		
		private SerializableSupplier<LocaleProperties.LocalePropertiesBuilder> builderSupplier;
		
		private String beanName;
		
		private List<Locale> enableLocaleList = new ArrayList<>();
		
		public LocalePropertiesBuilder builderSupplier(SerializableSupplier<LocaleProperties.LocalePropertiesBuilder> builderSupplier) {
			this.builderSupplier = builderSupplier;
			return this;
		}
		
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
				this.builderSupplier,
				this.beanName,
				this.enableLocaleList == null ? new ArrayList<>() : this.enableLocaleList
			);
		}
	}
	
}
