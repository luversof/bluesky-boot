package io.github.luversof.boot.uuid;

import java.util.HashMap;
import java.util.Map;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.AbstractBlueskyGroupProperties;

@ConfigurationProperties(prefix = UuidGeneratorProperties.PREFIX)
public class UuidGeneratorGroupProperties extends
		AbstractBlueskyGroupProperties<UuidGeneratorProperties, UuidGeneratorProperties.UuidGeneratorPropertiesBuilder> {

	private static final long serialVersionUID = 1L;

	private UuidGeneratorProperties parent;

	private Map<String, UuidGeneratorProperties> groups = new HashMap<>();

	@Autowired
	public void setParent(UuidGeneratorProperties parent) {
		this.parent = parent;
	}

	public UuidGeneratorProperties getParent() {
		return parent;
	}

	public Map<String, UuidGeneratorProperties> getGroups() {
		return groups;
	}

	public void setGroups(Map<String, UuidGeneratorProperties> groups) {
		this.groups = groups;
	}

	@Override
	protected UuidGeneratorProperties.UuidGeneratorPropertiesBuilder getBuilder(String groupName) {
		var groupModuleInfoMap = BlueskyBootContextHolder.getContext().getGroupModuleInfoMap();
		return groupModuleInfoMap.containsKey(groupName)
				? groupModuleInfoMap.get(groupName).getUuidGeneratorPropertiesBuilder()
				: UuidGeneratorProperties.builder();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;
		UuidGeneratorGroupProperties that = (UuidGeneratorGroupProperties) o;
		return Objects.equals(parent, that.parent) && Objects.equals(groups, that.groups);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), parent, groups);
	}

	@Override
	public String toString() {
		return "UuidGeneratorGroupProperties{" +
				"parent=" + parent +
				", groups=" + groups +
				'}';
	}
}
