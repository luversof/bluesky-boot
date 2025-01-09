package io.github.luversof.boot.web;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.convert.DurationUnit;

import io.github.luversof.boot.core.BlueskyProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "brick-boot.web.cookie")
public class CookieProperties implements BlueskyProperties, BeanNameAware {
	
	private static final long serialVersionUID = 1L;
	
	public static final String DEFAULT_BEAN_NAME = "cookieProperties";
	public static final String EXTERNAL_COOKIE_BEAN_NAME = "externalCookieProperties";
	
	private String beanName;
	
	private String name;
	
	@DurationUnit(ChronoUnit.SECONDS)
	private Duration maxAge;
	
	private String domain;
	
	private String path;
	
	private Boolean secure;
	
	private Boolean httpOnly;
	
	private String sameSite;
	
	public static CookiePropertiesBuilder builder() {
		return new CookiePropertiesBuilder();
	}
	
	public static class CookiePropertiesBuilder {
	
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
		
		public CookieProperties build() {
			return new CookieProperties(
				beanName, name, maxAge, domain, path, secure, httpOnly, sameSite
			);
		}
	}

}