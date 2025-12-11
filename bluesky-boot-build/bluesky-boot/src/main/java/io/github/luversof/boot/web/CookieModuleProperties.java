package io.github.luversof.boot.web;

import java.util.HashMap;
import java.util.Map;

import java.util.Objects;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.AbstractBlueskyModuleProperties;
import io.github.luversof.boot.util.function.SerializableFunction;

@ConfigurationProperties(prefix = CookieProperties.PREFIX)
public class CookieModuleProperties
		extends AbstractBlueskyModuleProperties<CookieProperties, CookieProperties.CookiePropertiesBuilder>
		implements BeanNameAware {

	private static final long serialVersionUID = 1L;

	public static final String DEFAULT_BEAN_NAME = "bluesky-boot.web.cookie-io.github.luversof.boot.web.CookieModuleProperties";
	public static final String EXTERNAL_COOKIE_BEAN_NAME = "bluesky-boot.web.external-cookie-io.github.luversof.boot.web.ExternalCookieModuleProperties";

	private String beanName;

	private CookieProperties parent;

	private Map<String, CookieProperties> modules = new HashMap<>();

	@Autowired
	@Qualifier(CookieProperties.DEFAULT_BEAN_NAME)
	public void setParent(CookieProperties parent) {
		this.parent = parent;
	}

	public CookieProperties getParent() {
		return parent;
	}

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public Map<String, CookieProperties> getModules() {
		return modules;
	}

	public void setModules(Map<String, CookieProperties> modules) {
		this.modules = modules;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;
		CookieModuleProperties that = (CookieModuleProperties) o;
		return Objects.equals(beanName, that.beanName) && Objects.equals(parent, that.parent)
				&& Objects.equals(modules, that.modules);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), beanName, parent, modules);
	}

	@Override
	public String toString() {
		return "CookieModuleProperties{" +
				"beanName='" + beanName + '\'' +
				", parent=" + parent +
				", modules=" + modules +
				'}';
	}

	protected SerializableFunction<String, CookieProperties.CookiePropertiesBuilder> getBuilderFunction() {
		return moduleName -> {
			var moduleInfoMap = BlueskyBootContextHolder.getContext().getModuleInfoMap();
			return moduleInfoMap.containsKey(moduleName) ? moduleInfoMap.get(moduleName).getCookiePropertiesBuilder()
					: CookieProperties.builder();
		};
	}

	@Override
	protected CookieProperties.CookiePropertiesBuilder getBuilder(String moduleName) {
		return getBuilderFunction().apply(moduleName);
	}
}
