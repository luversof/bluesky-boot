package io.github.luversof.boot.web;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.AbstractBlueskyModuleProperties;

@ConfigurationProperties(prefix = WebProperties.PREFIX)
public class WebModuleProperties
		extends AbstractBlueskyModuleProperties<WebProperties, WebProperties.WebPropertiesBuilder> {

	private static final long serialVersionUID = 1L;

	private WebProperties parent;

	private Map<String, WebProperties> modules = new HashMap<>();

	@Autowired
	public void setParent(WebProperties parent) {
		this.parent = parent;
	}

	public WebProperties getParent() {
		return parent;
	}

	public Map<String, WebProperties> getModules() {
		return modules;
	}

	public void setModules(Map<String, WebProperties> modules) {
		this.modules = modules;
	}

	@Override
	protected WebProperties.WebPropertiesBuilder getBuilder(String moduleName) {
		var moduleInfoMap = BlueskyBootContextHolder.getContext().getModuleInfoMap();
		return moduleInfoMap.containsKey(moduleName) ? moduleInfoMap.get(moduleName).getWebPropertiesBuilder()
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
		WebModuleProperties that = (WebModuleProperties) o;
		return Objects.equals(parent, that.parent) && Objects.equals(modules, that.modules);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), parent, modules);
	}

	@Override
	public String toString() {
		return "WebModuleProperties{" +
				"parent=" + parent +
				", modules=" + modules +
				'}';
	}
}
