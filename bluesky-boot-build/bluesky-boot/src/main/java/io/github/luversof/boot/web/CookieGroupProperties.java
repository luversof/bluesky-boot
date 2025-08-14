package io.github.luversof.boot.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.BlueskyGroupProperties;
import io.github.luversof.boot.util.function.SerializableFunction;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = CookieProperties.PREFIX)
public class CookieGroupProperties implements BlueskyGroupProperties<CookieProperties>, BeanNameAware  {

	private static final long serialVersionUID = 1L;
	
	private String beanName;
	
	@Autowired
	@Qualifier(CookieProperties.DEFAULT_BEAN_NAME)
	private CookieProperties parent;
	
	private Map<String, CookieProperties> groups = new HashMap<>();
	
	protected SerializableFunction<String, CookieProperties.CookiePropertiesBuilder> getBuilderFunction() {
		return groupName -> {
			var groupModuleInfoMap = BlueskyBootContextHolder.getContext().getGroupModuleInfoMap();
			return groupModuleInfoMap.containsKey(groupName) ? groupModuleInfoMap.get(groupName).getCookiePropertiesBuilder() : CookieProperties.builder();
		};
	}

	@Override
	public void load() {
		parentReload();
		
		BlueskyBootContextHolder.getContext().getGroupModules().keySet().forEach(groupName -> {
			var builder = getBuilderFunction().apply(groupName);
			
			// group이 없는 경우 기본 설정 추가
			if (!getGroups().containsKey(groupName)) {
				getGroups().put(groupName, builder.build());
			}
			
			var cookieProperties = getGroups().get(groupName);
			var propertyMapperConsumer = getParent().getPropertyMapperConsumer();
			propertyMapperConsumer.accept(getParent(), builder);
			propertyMapperConsumer.accept(cookieProperties, builder);
			
			getGroups().put(groupName, builder.build());
			
		});
	}
}
