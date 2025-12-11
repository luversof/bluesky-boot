package io.github.luversof.boot.web;

import java.util.HashMap;
import java.util.Map;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.AbstractBlueskyGroupProperties;

@ConfigurationProperties(prefix = DomainProperties.PREFIX)
public class DomainGroupProperties
		extends AbstractBlueskyGroupProperties<DomainProperties, DomainProperties.DomainPropertiesBuilder> {

	private static final long serialVersionUID = 1L;

	private DomainProperties parent;

	private Map<String, DomainProperties> groups = new HashMap<>();

	@Autowired
	public void setParent(DomainProperties parent) {
		this.parent = parent;
	}

	public DomainProperties getParent() {
		return parent;
	}

	public Map<String, DomainProperties> getGroups() {
		return groups;
	}

	public void setGroups(Map<String, DomainProperties> groups) {
		this.groups = groups;
	}

	@Override
	protected DomainProperties.DomainPropertiesBuilder getBuilder(String groupName) {
		var groupModuleInfoMap = BlueskyBootContextHolder.getContext().getGroupModuleInfoMap();
		return groupModuleInfoMap.containsKey(groupName)
				? groupModuleInfoMap.get(groupName).getDomainPropertiesBuilder()
				: DomainProperties.builder();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;
		DomainGroupProperties that = (DomainGroupProperties) o;
		return Objects.equals(parent, that.parent) && Objects.equals(groups, that.groups);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), parent, groups);
	}

	@Override
	public String toString() {
		return "DomainGroupProperties{" +
				"parent=" + parent +
				", groups=" + groups +
				'}';
	}
}
