package io.github.luversof.boot.context.i18n;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.AbstractBlueskyModuleProperties;
import io.github.luversof.boot.util.function.SerializableFunction;

@ConfigurationProperties(prefix = LocaleProperties.PREFIX)
public class LocaleModuleProperties
		extends AbstractBlueskyModuleProperties<LocaleProperties, LocaleProperties.LocalePropertiesBuilder>
		implements BeanNameAware {

	private static final long serialVersionUID = 1L;

	public static final String DEFAULT_BEAN_NAME = "bluesky-boot.locale-io.github.luversof.boot.context.i18n.LocaleModuleProperties";
	public static final String EXTERNAL_LOCALE_BEAN_NAME = "bluesky-boot.external-locale-io.github.luversof.boot.context.i18n.ExternalLocaleModuleProperties";

	private String beanName;

	private LocaleProperties parent;

	@Autowired
	public void setParent(@Qualifier(LocaleProperties.DEFAULT_BEAN_NAME) LocaleProperties localeProperties) {
		this.parent = localeProperties;
	}

	private Map<String, LocaleProperties> modules = new HashMap<>();

	@Override
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public String getBeanName() {
		return beanName;
	}

	public LocaleProperties getParent() {
		return parent;
	}

	public Map<String, LocaleProperties> getModules() {
		return modules;
	}

	public void setModules(Map<String, LocaleProperties> modules) {
		this.modules = modules;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;
		LocaleModuleProperties that = (LocaleModuleProperties) o;
		return (beanName != null ? beanName.equals(that.beanName) : that.beanName == null) &&
				(parent != null ? parent.equals(that.parent) : that.parent == null) &&
				(modules != null ? modules.equals(that.modules) : that.modules == null);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (beanName != null ? beanName.hashCode() : 0);
		result = 31 * result + (parent != null ? parent.hashCode() : 0);
		result = 31 * result + (modules != null ? modules.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "LocaleModuleProperties{" +
				"beanName='" + beanName + '\'' +
				", parent=" + parent +
				", modules=" + modules +
				'}';
	}

	protected SerializableFunction<String, LocaleProperties.LocalePropertiesBuilder> getBuilderFunction() {
		return moduleName -> {
			var moduleInfoMap = BlueskyBootContextHolder.getContext().getModuleInfoMap();
			return moduleInfoMap.containsKey(moduleName) ? moduleInfoMap.get(moduleName).getLocalePropertiesBuilder()
					: LocaleProperties.builder();
		};
	}

	@Override
	protected LocaleProperties.LocalePropertiesBuilder getBuilder(String moduleName) {
		return getBuilderFunction().apply(moduleName);
	}

}
