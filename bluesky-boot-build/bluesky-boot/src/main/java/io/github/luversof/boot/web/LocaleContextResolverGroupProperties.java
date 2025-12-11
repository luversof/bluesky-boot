package io.github.luversof.boot.web;

import java.util.HashMap;
import java.util.Map;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.AbstractBlueskyGroupProperties;

@ConfigurationProperties(prefix = LocaleContextResolverProperties.PREFIX)
public class LocaleContextResolverGroupProperties extends
		AbstractBlueskyGroupProperties<LocaleContextResolverProperties, LocaleContextResolverProperties.LocaleContextResolverPropertiesBuilder> {

	private static final long serialVersionUID = 1L;

	private LocaleContextResolverProperties parent;

	private Map<String, LocaleContextResolverProperties> groups = new HashMap<>();

	@Autowired
	public void setParent(LocaleContextResolverProperties parent) {
		this.parent = parent;
	}

	public LocaleContextResolverProperties getParent() {
		return parent;
	}

	public Map<String, LocaleContextResolverProperties> getGroups() {
		return groups;
	}

	public void setGroups(Map<String, LocaleContextResolverProperties> groups) {
		this.groups = groups;
	}

	@Override
	protected LocaleContextResolverProperties.LocaleContextResolverPropertiesBuilder getBuilder(String groupName) {
		var groupModuleInfoMap = BlueskyBootContextHolder.getContext().getGroupModuleInfoMap();
		return groupModuleInfoMap.containsKey(groupName)
				? groupModuleInfoMap.get(groupName).getLocaleContextResolverPropertiesBuilder()
				: LocaleContextResolverProperties.builder();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;
		LocaleContextResolverGroupProperties that = (LocaleContextResolverGroupProperties) o;
		return Objects.equals(parent, that.parent) && Objects.equals(groups, that.groups);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), parent, groups);
	}

	@Override
	public String toString() {
		return "LocaleContextResolverGroupProperties{" +
				"parent=" + parent +
				", groups=" + groups +
				'}';
	}
}
