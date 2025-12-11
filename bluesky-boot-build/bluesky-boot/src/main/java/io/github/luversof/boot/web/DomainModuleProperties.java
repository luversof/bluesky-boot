package io.github.luversof.boot.web;

import java.util.HashMap;
import java.util.Map;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.AbstractBlueskyModuleProperties;

@ConfigurationProperties(prefix = DomainProperties.PREFIX)
public class DomainModuleProperties
		extends AbstractBlueskyModuleProperties<DomainProperties, DomainProperties.DomainPropertiesBuilder> {

	private static final long serialVersionUID = 1L;

	private DomainProperties parent;

	private Map<String, DomainProperties> modules = new HashMap<>();

	@Autowired
	public void setParent(DomainProperties parent) {
		this.parent = parent;
	}

	public DomainProperties getParent() {
		return parent;
	}

	public Map<String, DomainProperties> getModules() {
		return modules;
	}

	public void setModules(Map<String, DomainProperties> modules) {
		this.modules = modules;
	}

	@Override
	protected DomainProperties.DomainPropertiesBuilder getBuilder(String moduleName) {
		var moduleInfoMap = BlueskyBootContextHolder.getContext().getModuleInfoMap();
		return moduleInfoMap.containsKey(moduleName) ? moduleInfoMap.get(moduleName).getDomainPropertiesBuilder()
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
		DomainModuleProperties that = (DomainModuleProperties) o;
		return Objects.equals(parent, that.parent) && Objects.equals(modules, that.modules);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), parent, modules);
	}

	@Override
	public String toString() {
		return "DomainModuleProperties{" +
				"parent=" + parent +
				", modules=" + modules +
				'}';
	}
}
