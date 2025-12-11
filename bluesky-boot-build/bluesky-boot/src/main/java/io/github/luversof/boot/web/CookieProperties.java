package io.github.luversof.boot.web;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.function.BiConsumer;

import java.util.Objects;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.convert.DurationUnit;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.AbstractBlueskyProperties;
import io.github.luversof.boot.core.BlueskyPropertiesBuilder;
import io.github.luversof.boot.util.function.SerializableSupplier;

@ConfigurationProperties(prefix = CookieProperties.PREFIX)
public class CookieProperties extends
		AbstractBlueskyProperties<CookieProperties, CookieProperties.CookiePropertiesBuilder> implements BeanNameAware {

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

	public CookieProperties() {
	}

	public CookieProperties(String beanName, String name, Duration maxAge, String domain, String path, Boolean secure,
			Boolean httpOnly, String sameSite) {
		this.beanName = beanName;
		this.name = name;
		this.maxAge = maxAge;
		this.domain = domain;
		this.path = path;
		this.secure = secure;
		this.httpOnly = httpOnly;
		this.sameSite = sameSite;
	}

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Duration getMaxAge() {
		return maxAge;
	}

	public void setMaxAge(Duration maxAge) {
		this.maxAge = maxAge;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Boolean getSecure() {
		return secure;
	}

	public void setSecure(Boolean secure) {
		this.secure = secure;
	}

	public Boolean getHttpOnly() {
		return httpOnly;
	}

	public void setHttpOnly(Boolean httpOnly) {
		this.httpOnly = httpOnly;
	}

	public String getSameSite() {
		return sameSite;
	}

	public void setSameSite(String sameSite) {
		this.sameSite = sameSite;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;
		CookieProperties that = (CookieProperties) o;
		return Objects.equals(beanName, that.beanName) && Objects.equals(name, that.name)
				&& Objects.equals(maxAge, that.maxAge) && Objects.equals(domain, that.domain)
				&& Objects.equals(path, that.path) && Objects.equals(secure, that.secure)
				&& Objects.equals(httpOnly, that.httpOnly) && Objects.equals(sameSite, that.sameSite);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), beanName, name, maxAge, domain, path, secure, httpOnly, sameSite);
	}

	@Override
	public String toString() {
		return "CookieProperties{" +
				"beanName='" + beanName + '\'' +
				", name='" + name + '\'' +
				", maxAge=" + maxAge +
				", domain='" + domain + '\'' +
				", path='" + path + '\'' +
				", secure=" + secure +
				", httpOnly=" + httpOnly +
				", sameSite='" + sameSite + '\'' +
				'}';
	}

	protected BiConsumer<CookieProperties, CookiePropertiesBuilder> getPropertyMapperConsumer() {
		var propertyMapper = PropertyMapper.get();

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
			return parentModuleInfo == null ? CookieProperties.builder()
					: parentModuleInfo.getCookiePropertiesBuilder();
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
					beanName, name, maxAge, domain, path, secure, httpOnly, sameSite);
		}
	}

}