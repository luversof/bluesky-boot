package io.github.luversof.boot.context.i18n;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.BlueskyGroupProperties;
import io.github.luversof.boot.util.function.SerializableFunction;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = "bluesky-boot.locale")
public class LocaleGroupProperties implements BlueskyGroupProperties<LocaleProperties> {
	
	private static final long serialVersionUID = 1L;
	
	public static final String DEFAULT_BEAN_NAME = "bluesky-boot.locale-io.github.luversof.boot.context.i18n.LocaleGroupProperties";
	public static final String EXTERNAL_LOCALE_BEAN_NAME = "bluesky-boot.external-locale-io.github.luversof.boot.context.i18n.ExternalLocaleGroupProperties";

	@Autowired
	private LocaleProperties parent;
	
	private Map<String, LocaleProperties> groups = new HashMap<>();
	
	protected SerializableFunction<String, LocaleProperties.LocalePropertiesBuilder> getBuilderFunction() {
		return groupName -> {
			var groupModuleInfoMap = BlueskyBootContextHolder.getContext().getGroupModuleInfoMap();
			return groupModuleInfoMap.containsKey(groupName) ? groupModuleInfoMap.get(groupName).getLocalePropertiesBuilder() : LocaleProperties.builder();
		};
	}

	@Override
	public void load() {
		parentReload();
		
		// group이 없는 경우 기본 설정 추가
		BlueskyBootContextHolder.getContext().getGroupModules().keySet().forEach(groupName -> {
			var builder = getBuilderFunction().apply(groupName);
			
			if (!getGroups().containsKey(groupName)) {
				getGroups().put(groupName, builder.build());
			}
			
			var localeProperties = getGroups().get(groupName);
			var propertyMapperConsumer = getParent().getPropertyMapperConsumer();
			propertyMapperConsumer.accept(getParent(), builder);
			propertyMapperConsumer.accept(localeProperties, builder);
			
			getGroups().put(groupName, builder.build());
			
		});
	}
	
}
