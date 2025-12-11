package io.github.luversof.boot.web;

import java.util.HashMap;
import java.util.Map;

import java.util.Objects;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.AbstractBlueskyGroupProperties;

@ConfigurationProperties(prefix = LocaleResolveHandlerProperties.PREFIX)
public class LocaleResolveHandlerGroupProperties extends
		AbstractBlueskyGroupProperties<LocaleResolveHandlerProperties, LocaleResolveHandlerProperties.LocaleResolveHandlerPropertiesBuilder>
		implements BeanNameAware {

	private static final long serialVersionUID = 1L;

	private String beanName;

	private LocaleResolveHandlerProperties parent;

	private Map<String, LocaleResolveHandlerProperties> groups = new HashMap<>();

	public LocaleResolveHandlerGroupProperties(LocaleResolveHandlerProperties parent) {
		this.parent = parent;
	}

	public String getBeanName() {
		return beanName;
	}

	@Override
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public LocaleResolveHandlerProperties getParent() {
		return parent;
	}

	public void setParent(LocaleResolveHandlerProperties parent) {
		this.parent = parent;
	}

	public Map<String, LocaleResolveHandlerProperties> getGroups() {
		return groups;
	}

	public void setGroups(Map<String, LocaleResolveHandlerProperties> groups) {
		this.groups = groups;
	}

	@Override
	protected LocaleResolveHandlerProperties.LocaleResolveHandlerPropertiesBuilder getBuilder(String groupName) {
		var groupModuleInfoMap = BlueskyBootContextHolder.getContext().getGroupModuleInfoMap();
		return groupModuleInfoMap.containsKey(groupName)
				? groupModuleInfoMap.get(groupName).getLocaleResolveHandlerPropertiesBuilder()
				: LocaleResolveHandlerProperties.builder();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;
		LocaleResolveHandlerGroupProperties that = (LocaleResolveHandlerGroupProperties) o;
		return Objects.equals(beanName, that.beanName) && Objects.equals(parent, that.parent)
				&& Objects.equals(groups, that.groups);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), beanName, parent, groups);
	}

	@Override
	public String toString() {
		return "LocaleResolveHandlerGroupProperties{" +
				"beanName='" + beanName + '\'' +
				", parent=" + parent +
				", groups=" + groups +
				'}';
	}
}
