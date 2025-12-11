package io.github.luversof.boot.context.i18n;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.AbstractBlueskyGroupProperties;
import io.github.luversof.boot.util.function.SerializableFunction;

@ConfigurationProperties(prefix = LocaleProperties.PREFIX)
public class LocaleGroupProperties
		extends AbstractBlueskyGroupProperties<LocaleProperties, LocaleProperties.LocalePropertiesBuilder>
		implements BeanNameAware {

	private static final long serialVersionUID = 1L;

	private String beanName;

	private LocaleProperties parent;

	@Autowired
	@Qualifier(LocaleProperties.DEFAULT_BEAN_NAME)
	public void setParent(LocaleProperties parent) {
		this.parent = parent;
	}

	private Map<String, LocaleProperties> groups = new HashMap<>();

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

	public Map<String, LocaleProperties> getGroups() {
		return groups;
	}

	public void setGroups(Map<String, LocaleProperties> groups) {
		this.groups = groups;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		if (!super.equals(o))
			return false;
		LocaleGroupProperties that = (LocaleGroupProperties) o;
		return (beanName != null ? beanName.equals(that.beanName) : that.beanName == null) &&
				(parent != null ? parent.equals(that.parent) : that.parent == null) &&
				(groups != null ? groups.equals(that.groups) : that.groups == null);
	}

	@Override
	public int hashCode() {
		int result = super.hashCode();
		result = 31 * result + (beanName != null ? beanName.hashCode() : 0);
		result = 31 * result + (parent != null ? parent.hashCode() : 0);
		result = 31 * result + (groups != null ? groups.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "LocaleGroupProperties{" +
				"beanName='" + beanName + '\'' +
				", parent=" + parent +
				", groups=" + groups +
				'}';
	}

	protected SerializableFunction<String, LocaleProperties.LocalePropertiesBuilder> getBuilderFunction() {
		return groupName -> {
			var groupModuleInfoMap = BlueskyBootContextHolder.getContext().getGroupModuleInfoMap();
			return groupModuleInfoMap.containsKey(groupName)
					? groupModuleInfoMap.get(groupName).getLocalePropertiesBuilder()
					: LocaleProperties.builder();
		};
	}

	@Override
	protected LocaleProperties.LocalePropertiesBuilder getBuilder(String groupName) {
		return getBuilderFunction().apply(groupName);
	}
}
