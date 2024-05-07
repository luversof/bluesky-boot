package io.github.luversof.boot.web;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.BlueskyModuleProperties;
import lombok.Data;
import lombok.Setter;

@Data
@ConfigurationProperties(prefix = "bluesky-boot.domain")
public class DomainModuleProperties implements BlueskyModuleProperties<DomainProperties> {
	
	@Setter(onMethod_ = { @Autowired })
	private DomainProperties parent;
	
	private Map<String, DomainProperties> modules = new HashMap<>();

	@Override
	public void afterPropertiesSet() throws Exception {
		
		var moduleInfoMap = BlueskyBootContextHolder.getContext().getModuleInfoMap();
		
		var propertyMapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
		
		BlueskyBootContextHolder.getContext().getModuleNameSet().forEach(moduleName -> {
			var builder = moduleInfoMap.containsKey(moduleName) ? moduleInfoMap.get(moduleName).getDomainPropertiesBuilder() : DomainProperties.builder();
			
			if (!getModules().containsKey(moduleName)) {
				getModules().put(moduleName, builder.build());
			}
			
			var domainModuleProperties = getModules().get(moduleName);
			
			propertyMapper.from(getParent()::getAddPathPatterns).to(builder::addPathPatterns);
			propertyMapper.from(domainModuleProperties::getAddPathPatterns).to(builder::addPathPatterns);
			propertyMapper.from(getParent()::getWebList).to(builder::webList);
			propertyMapper.from(domainModuleProperties::getWebList).to(builder::webList);
			propertyMapper.from(getParent()::getMobileWebList).to(builder::mobileWebList);
			propertyMapper.from(domainModuleProperties::getMobileWebList).to(builder::mobileWebList);
			propertyMapper.from(getParent()::getDevDomainList).to(builder::devDomainList);
			propertyMapper.from(domainModuleProperties::getDevDomainList).to(builder::devDomainList);
			propertyMapper.from(getParent()::getStaticPathList).to(builder::staticPathList);
			propertyMapper.from(domainModuleProperties::getStaticPathList).to(builder::staticPathList);
			propertyMapper.from(getParent()::getExcludePathList).to(builder::excludePathList);
			propertyMapper.from(domainModuleProperties::getExcludePathList).to(builder::excludePathList);
			propertyMapper.from(getParent()::getRequestPath).to(builder::requestPath);
			propertyMapper.from(domainModuleProperties::getRequestPath).to(builder::requestPath);
			propertyMapper.from(getParent()::getForwardPath).to(builder::forwardPath);
			propertyMapper.from(domainModuleProperties::getForwardPath).to(builder::forwardPath);
			
			getModules().put(moduleName, builder.build());
		});
	}

	
}
