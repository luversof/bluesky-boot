package io.github.luversof.boot.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.BlueskyModuleProperties;
import io.github.luversof.boot.util.function.SerializableFunction;
import lombok.Data;

@Data
@ConfigurationProperties(prefix = "brick-boot.web.cookie")
public class CookieModuleProperties implements BlueskyModuleProperties<CookieProperties>, BeanNameAware {
	
	private static final long serialVersionUID = 1L;
	
	public static final String DEFAULT_BEAN_NAME = "cookieModuleProperties";
	public static final String EXTERNAL_COOKIE_BEAN_NAME = "externalCookieModuleProperties";
	
	private final SerializableFunction<String, CookieProperties.CookiePropertiesBuilder> builderFunction;
	
	private String beanName;

	private CookieProperties parent;
	
	private Map<String, CookieProperties> modules = new HashMap<>();
	
	public CookieModuleProperties(CookieProperties parent, SerializableFunction<String, CookieProperties.CookiePropertiesBuilder> builderFunction) {
		this.parent = parent;
		this.builderFunction = builderFunction;
	}

	@Override
	public void load() {
		parentReload();
		var blueskyBootContext = BlueskyBootContextHolder.getContext();
		var moduleNameSet = blueskyBootContext.getModuleNameSet();
		var propertyMapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
		
		moduleNameSet.forEach(moduleName -> {
			var builder = builderFunction.apply(moduleName);
			
			if (!getModules().containsKey(moduleName)) {
				getModules().put(moduleName, builder.build());
			}
			
			var cookieProperties = getModules().get(moduleName);
			
			propertyMapper.from(getParent()::getBuilderSupplier).to(builder::builderSupplier);
			propertyMapper.from(cookieProperties::getBuilderSupplier).to(builder::builderSupplier);
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
