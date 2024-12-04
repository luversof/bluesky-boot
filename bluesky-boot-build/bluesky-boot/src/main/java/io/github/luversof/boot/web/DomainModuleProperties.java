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
@ConfigurationProperties(prefix = "bluesky-boot.web.domain")
public class DomainModuleProperties implements BlueskyModuleProperties<DomainProperties> {

	private static final long serialVersionUID = 1L;

	private String beanName;
	
	@Setter(onMethod_ = @Autowired)
	private DomainProperties parent;
	
	private Map<String, DomainProperties> modules = new HashMap<>();

	@Override
	public void load() {
		var blueskyBootContext = BlueskyBootContextHolder.getContext();
		var moduleNameSet = blueskyBootContext.getModuleNameSet();
		var moduleInfoMap = blueskyBootContext.getModuleInfoMap();
		
		var propertyMapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
		
		moduleNameSet.forEach(moduleName -> {
			var builder = moduleInfoMap.containsKey(moduleName) ? moduleInfoMap.get(moduleName).getDomainPropertiesBuilder() : DomainProperties.builder();
			
			if (!getModules().containsKey(moduleName)) {
				getModules().put(moduleName, builder.build());
			}
			
			var domainProperties = getModules().get(moduleName);
			
			propertyMapper.from(getParent()::getAddPathPatterns).to(builder::addPathPatterns);
			propertyMapper.from(domainProperties::getAddPathPatterns).to(builder::addPathPatterns);
			propertyMapper.from(getParent()::getWebList).whenNot(x -> x == null || x.isEmpty()).to(builder::webList);
			propertyMapper.from(domainProperties::getWebList).whenNot(x -> x == null || x.isEmpty()).to(builder::webList);
			propertyMapper.from(getParent()::getMobileWebList).whenNot(x -> x == null || x.isEmpty()).to(builder::mobileWebList);
			propertyMapper.from(domainProperties::getMobileWebList).whenNot(x -> x == null || x.isEmpty()).to(builder::mobileWebList);
			propertyMapper.from(getParent()::getDevDomainList).whenNot(x -> x == null || x.isEmpty()).to(builder::devDomainList);
			propertyMapper.from(domainProperties::getDevDomainList).whenNot(x -> x == null || x.isEmpty()).to(builder::devDomainList);
			propertyMapper.from(getParent()::getStaticPathList).whenNot(x -> x == null || x.isEmpty()).to(builder::staticPathList);
			propertyMapper.from(domainProperties::getStaticPathList).whenNot(x -> x == null || x.isEmpty()).to(builder::staticPathList);
			propertyMapper.from(getParent()::getExcludePathList).whenNot(x -> x == null || x.isEmpty()).to(builder::excludePathList);
			propertyMapper.from(domainProperties::getExcludePathList).whenNot(x -> x == null || x.isEmpty()).to(builder::excludePathList);
			propertyMapper.from(getParent()::getRequestPath).to(builder::requestPath);
			propertyMapper.from(domainProperties::getRequestPath).to(builder::requestPath);
			propertyMapper.from(getParent()::getForwardPath).to(builder::forwardPath);
			propertyMapper.from(domainProperties::getForwardPath).to(builder::forwardPath);
			
			getModules().put(moduleName, builder.build());
		});
	}

	
}
