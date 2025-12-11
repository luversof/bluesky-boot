package io.github.luversof.boot.uuid;

import java.util.HashMap;
import java.util.Map;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.AbstractBlueskyModuleProperties;

@ConfigurationProperties(prefix = UuidGeneratorProperties.PREFIX)
public class UuidGeneratorModuleProperties extends
		AbstractBlueskyModuleProperties<UuidGeneratorProperties, UuidGeneratorProperties.UuidGeneratorPropertiesBuilder> {

	private static final long serialVersionUID = 1L;

	private UuidGeneratorProperties parent;

	private Map<String, UuidGeneratorProperties> modules = new HashMap<>();

	@Autowired
	public void setParent(UuidGeneratorProperties parent) {
		this.parent = parent;
	}

	public UuidGeneratorProperties getParent() {
		return parent;
	}

	public Map<String, UuidGeneratorProperties> getModules() {
		return modules;
	}

	public void setModules(Map<String, UuidGeneratorProperties> modules) {
		this.modules = modules;
	}

	@Override
	protected UuidGeneratorProperties.UuidGeneratorPropertiesBuilder getBuilder(String moduleName) {
		var moduleInfoMap = BlueskyBootContextHolder.getContext().getModuleInfoMap();
		return moduleInfoMap.containsKey(moduleName) ? moduleInfoMap.get(moduleName).getUuidGeneratorPropertiesBuilder()
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
		UuidGeneratorModuleProperties that = (UuidGeneratorModuleProperties) o;
		return Objects.equals(parent, that.parent) && Objects.equals(modules, that.modules);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), parent, modules);
	}

	@Override
	public String toString() {
		return "UuidGeneratorModuleProperties{" +
				"parent=" + parent +
				", modules=" + modules +
				'}';
	}
}
