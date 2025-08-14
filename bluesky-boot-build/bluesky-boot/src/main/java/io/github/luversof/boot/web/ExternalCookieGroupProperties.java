package io.github.luversof.boot.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.util.function.SerializableFunction;

@ConfigurationProperties(prefix = ExternalCookieProperties.PREFIX)
public class ExternalCookieGroupProperties extends CookieGroupProperties {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Dependency 참조 순서 처리를 위해 선언
	 */
	@Autowired
	public void setExternalCookieProperties(ExternalCookieProperties externalCookieProperties) {
		setParent(externalCookieProperties);
	}

	@Override
	protected SerializableFunction<String, CookieProperties.CookiePropertiesBuilder> getBuilderFunction() {
		return groupName -> {
			var groupModuleInfoMap = BlueskyBootContextHolder.getContext().getGroupModuleInfoMap();
			return groupModuleInfoMap.containsKey(groupName) ? groupModuleInfoMap.get(groupName).getExternalCookiePropertiesBuilder() : CookieProperties.builder();
		};
	}

}