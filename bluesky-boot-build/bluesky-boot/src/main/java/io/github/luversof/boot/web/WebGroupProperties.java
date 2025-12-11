package io.github.luversof.boot.web;

import java.util.HashMap;
import java.util.Map;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.AbstractBlueskyGroupProperties;

@ConfigurationProperties(prefix = WebProperties.PREFIX)
public class WebGroupProperties
		extends AbstractBlueskyGroupProperties<WebProperties, WebProperties.WebPropertiesBuilder> {

	private static final long serialVersionUID = 1L;

	private WebProperties parent;

	private Map<String, WebProperties> groups = new HashMap<>();

	@Autowired
	public void setParent(WebProperties parent) {
		this.parent = parent;
	}

	public WebProperties getParent() {
		return parent;
	}

	public Map<String, WebProperties> getGroups() {
		return groups;
	}

	public void setGroups(Map<String, WebProperties> groups) {
		this.groups = groups;
	}

	@Override
	protected WebProperties.WebPropertiesBuilder getBuilder(String groupName) {
		var groupModuleInfoMap = BlueskyBootContextHolder.getContext().getGroupModuleInfoMap();
		return groupModuleInfoMap.containsKey(groupName) ? groupModuleInfoMap.get(groupName).getWebPropertiesBuilder()
				: WebProperties.builder();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;
		WebGroupProperties that = (WebGroupProperties) o;
		return Objects.equals(parent, that.parent) && Objects.equals(groups, that.groups);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), parent, groups);
	}

	@Override
	public String toString() {
		return "WebGroupProperties{" +
				"parent=" + parent +
				", groups=" + groups +
				'}';
	}
}
