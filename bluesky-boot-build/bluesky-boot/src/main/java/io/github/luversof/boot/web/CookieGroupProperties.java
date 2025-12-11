package io.github.luversof.boot.web;

import java.util.HashMap;
import java.util.Map;

import java.util.Objects;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.AbstractBlueskyGroupProperties;
import io.github.luversof.boot.util.function.SerializableFunction;

@ConfigurationProperties(prefix = CookieProperties.PREFIX)
public class CookieGroupProperties
		extends AbstractBlueskyGroupProperties<CookieProperties, CookieProperties.CookiePropertiesBuilder>
		implements BeanNameAware {

	private static final long serialVersionUID = 1L;

	private String beanName;

	private CookieProperties parent;

	private Map<String, CookieProperties> groups = new HashMap<>();

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

	public Map<String, CookieProperties> getGroups() {
		return groups;
	}

	public void setGroups(Map<String, CookieProperties> groups) {
		this.groups = groups;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;
		CookieGroupProperties that = (CookieGroupProperties) o;
		return Objects.equals(beanName, that.beanName) && Objects.equals(parent, that.parent)
				&& Objects.equals(groups, that.groups);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), beanName, parent, groups);
	}

	@Override
	public String toString() {
		return "CookieGroupProperties{" +
				"beanName='" + beanName + '\'' +
				", parent=" + parent +
				", groups=" + groups +
				'}';
	}

	protected SerializableFunction<String, CookieProperties.CookiePropertiesBuilder> getBuilderFunction() {
		return groupName -> {
			var groupModuleInfoMap = BlueskyBootContextHolder.getContext().getGroupModuleInfoMap();
			return groupModuleInfoMap.containsKey(groupName)
					? groupModuleInfoMap.get(groupName).getCookiePropertiesBuilder()
					: CookieProperties.builder();
		};
	}

	@Override
	protected CookieProperties.CookiePropertiesBuilder getBuilder(String groupName) {
		return getBuilderFunction().apply(groupName);
	}
}
