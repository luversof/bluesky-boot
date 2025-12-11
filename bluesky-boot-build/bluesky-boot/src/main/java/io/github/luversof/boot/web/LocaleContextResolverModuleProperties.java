package io.github.luversof.boot.web;

import java.util.HashMap;
import java.util.Map;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.AbstractBlueskyModuleProperties;

@ConfigurationProperties(prefix = LocaleContextResolverProperties.PREFIX)
public class LocaleContextResolverModuleProperties extends
		AbstractBlueskyModuleProperties<LocaleContextResolverProperties, LocaleContextResolverProperties.LocaleContextResolverPropertiesBuilder> {

	private static final long serialVersionUID = 1L;

	private LocaleContextResolverProperties parent;

	private Map<String, LocaleContextResolverProperties> modules = new HashMap<>();

	@Autowired
	public void setParent(LocaleContextResolverProperties parent) {
		this.parent = parent;
	}

	public LocaleContextResolverProperties getParent() {
		return parent;
	}

	public Map<String, LocaleContextResolverProperties> getModules() {
		return modules;
	}

	public void setModules(Map<String, LocaleContextResolverProperties> modules) {
		this.modules = modules;
	}

	@Override
	protected LocaleContextResolverProperties.LocaleContextResolverPropertiesBuilder getBuilder(String moduleName) {
		var moduleInfoMap = BlueskyBootContextHolder.getContext().getModuleInfoMap();
		return moduleInfoMap.containsKey(moduleName)
				? moduleInfoMap.get(moduleName).getLocaleContextResolverPropertiesBuilder()
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
		LocaleContextResolverModuleProperties that = (LocaleContextResolverModuleProperties) o;
		return Objects.equals(parent, that.parent) && Objects.equals(modules, that.modules);
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(), parent, modules);
	}

	@Override
	public String toString() {
		return "LocaleContextResolverModuleProperties{" +
				"parent=" + parent +
				", modules=" + modules +
				'}';
	}
}
