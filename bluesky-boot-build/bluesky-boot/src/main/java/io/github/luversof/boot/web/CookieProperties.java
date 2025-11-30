package io.github.luversof.boot.web;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.function.BiConsumer;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.convert.DurationUnit;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.AbstractBlueskyProperties;
import io.github.luversof.boot.core.BlueskyPropertiesBuilder;
import io.github.luversof.boot.util.function.SerializableSupplier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = CookieProperties.PREFIX)
public class CookieProperties extends AbstractBlueskyProperties<CookieProperties, CookieProperties.CookiePropertiesBuilder> implements BeanNameAware {
	
	private static final long serialVersionUID = 1L;
	
	public static final String PREFIX = "bluesky-boot.web.cookie";
	
	public static final String DEFAULT_BEAN_NAME = "bluesky-boot.web.cookie-io.github.luversof.boot.web.CookieProperties";
	public static final String EXTERNAL_COOKIE_BEAN_NAME = "bluesky-boot.web.external-cookie-io.github.luversof.boot.web.ExternalCookieProperties";
	
	private String beanName;
	
	private String name;
	
	@DurationUnit(ChronoUnit.SECONDS)
	private Duration maxAge;
	
	private String domain;
	
	private String path;
	
	private Boolean secure;
	
	private Boolean httpOnly;
	
	private String sameSite;
	
	protected BiConsumer<CookieProperties, CookiePropertiesBuilder> getPropertyMapperConsumer() {
		var propertyMapper = PropertyMapper.get().alwaysApplyingWhenNonNull();

		return (properties, builder) -> {
			if (properties == null) {
				return;
			}
			propertyMapper.from(properties::getBeanName).to(builder::beanName);
			propertyMapper.from(properties::getName).to(builder::name);
			propertyMapper.from(properties::getMaxAge).to(builder::maxAge);
			propertyMapper.from(properties::getDomain).to(builder::domain);
			propertyMapper.from(properties::getPath).to(builder::path);
			propertyMapper.from(properties::getSecure).to(builder::secure);
			propertyMapper.from(properties::getHttpOnly).to(builder::httpOnly);
			propertyMapper.from(properties::getSameSite).to(builder::sameSite);
		};
	}
	
	@Override
	protected CookiePropertiesBuilder getBuilder() {
		return getBuilderSupplier().get();
	}
	
	protected SerializableSupplier<CookieProperties.CookiePropertiesBuilder> getBuilderSupplier() {
		return () -> {
			var parentModuleInfo = BlueskyBootContextHolder.getContext().getParentModuleInfo();
			return parentModuleInfo == null ? CookieProperties.builder() : parentModuleInfo.getCookiePropertiesBuilder();
		};
	}
	
	public static CookiePropertiesBuilder builder() {
		return new CookiePropertiesBuilder();
	}
	
	public static class CookiePropertiesBuilder implements BlueskyPropertiesBuilder<CookieProperties> {
		
		private String beanName;
		
		private String name;
		
		@DurationUnit(ChronoUnit.SECONDS)
		private Duration maxAge;
		
		private String domain;
		
		private String path;
		
		private Boolean secure;
		
		private Boolean httpOnly;
		
		private String sameSite;
		
		public CookiePropertiesBuilder beanName(String beanName) {
			this.beanName = beanName;
			return this;
		}
	
		public CookiePropertiesBuilder name(String name) {
			this.name = name;
			return this;
		}
		
		public CookiePropertiesBuilder maxAge(Duration maxAge) {
			this.maxAge = maxAge;
			return this;
		}
		
		public CookiePropertiesBuilder domain(String domain) {
			this.domain = domain;
			return this;
		}
		
		public CookiePropertiesBuilder path(String path) {
			this.path = path;
			return this;
		}
		
		public CookiePropertiesBuilder secure(Boolean secure) {
			this.secure = secure;
			return this;
		}
		
		public CookiePropertiesBuilder httpOnly(Boolean httpOnly) {
			this.httpOnly = httpOnly;
			return this;
		}
		
		public CookiePropertiesBuilder sameSite(String sameSite) {
			this.sameSite = sameSite;
			return this;
		}
		
		@Override
		public CookieProperties build() {
			return new CookieProperties(
				beanName, name, maxAge, domain, path, secure, httpOnly, sameSite
			);
		}
	}

}