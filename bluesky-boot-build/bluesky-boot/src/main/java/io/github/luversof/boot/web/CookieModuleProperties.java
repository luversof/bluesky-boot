package io.github.luversof.boot.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.BlueskyModuleProperties;
import io.github.luversof.boot.util.function.SerializableFunction;
import lombok.Data;
import lombok.Setter;

@Data
@ConfigurationProperties(prefix = "brick-boot.web.cookie")
public class CookieModuleProperties implements BlueskyModuleProperties<CookieProperties>, BeanNameAware {
	
	private static final long serialVersionUID = 1L;
	
	public static final String DEFAULT_BEAN_NAME = "brick-boot.web.cookie-io.github.luversof.boot.web.CookieModuleProperties";
	public static final String EXTERNAL_COOKIE_BEAN_NAME = "brick-boot.web.external-cookie-io.github.luversof.boot.web.ExternalCookieModuleProperties";
	
	private String beanName;

	@Setter(onMethod_ = { @Autowired, @Qualifier(CookieProperties.DEFAULT_BEAN_NAME) }) // NOSONAR
	private CookieProperties parent;
	
	private Map<String, CookieProperties> modules = new HashMap<>();
	
	
	protected SerializableFunction<String, CookieProperties.CookiePropertiesBuilder> getBuilderFunction() {
		return moduleName -> {
			var moduleInfoMap = BlueskyBootContextHolder.getContext().getModuleInfoMap();
			return moduleInfoMap.containsKey(moduleName) ? moduleInfoMap.get(moduleName).getCookiePropertiesBuilder() : CookieProperties.builder();
		};
	}

	@Override
	public void load() {
		parentReload();
		var blueskyBootContext = BlueskyBootContextHolder.getContext();
		var moduleNameSet = blueskyBootContext.getModuleNameSet();
		var propertyMapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
		
		moduleNameSet.forEach(moduleName -> {
			var builder = getBuilderFunction().apply(moduleName);
			
			if (!getModules().containsKey(moduleName)) {
				getModules().put(moduleName, builder.build());
			}
			
			var cookieProperties = getModules().get(moduleName);
			
			propertyMapper.from(getParent()::getBeanName).to(builder::beanName);
			propertyMapper.from(cookieProperties::getBeanName).to(builder::beanName);
			propertyMapper.from(getParent()::getName).to(builder::name);
			propertyMapper.from(cookieProperties::getName).to(builder::name);
			propertyMapper.from(getParent()::getMaxAge).to(builder::maxAge);
			propertyMapper.from(cookieProperties::getMaxAge).to(builder::maxAge);
			propertyMapper.from(getParent()::getDomain).to(builder::domain);
			propertyMapper.from(cookieProperties::getDomain).to(builder::domain);
			propertyMapper.from(getParent()::getPath).to(builder::path);
			propertyMapper.from(cookieProperties::getPath).to(builder::path);
			propertyMapper.from(getParent()::getSecure).to(builder::secure);
			propertyMapper.from(cookieProperties::getSecure).to(builder::secure);
			propertyMapper.from(getParent()::getHttpOnly).to(builder::httpOnly);
			propertyMapper.from(cookieProperties::getHttpOnly).to(builder::httpOnly);
			propertyMapper.from(getParent()::getSameSite).to(builder::sameSite);
			propertyMapper.from(cookieProperties::getSameSite).to(builder::sameSite);
			
			getModules().put(moduleName, builder.build());
			
		});
	}
}
