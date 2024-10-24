package io.github.luversof.boot.web.servlet.support;

import java.util.Map.Entry;

import io.github.luversof.boot.context.ApplicationContextUtil;
import io.github.luversof.boot.web.DomainModuleProperties;
import io.github.luversof.boot.web.DomainProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractModuleNameResolver implements ModuleNameResolver {

	@Override
	public String resolve(HttpServletRequest request) {
		var modulePropertiesEntry = getModulePropertiesEntry(request);
		return modulePropertiesEntry == null ? null : modulePropertiesEntry.getKey();
	}
	
	/**
	 * 현재 요청에 대한 액티브 모듈 조회
	 * @param request
	 * @return
	 */
	private Entry<String, DomainProperties> getModulePropertiesEntry(HttpServletRequest request) {
		var domainModuleProperties = ApplicationContextUtil.getApplicationContext().getBean(DomainModuleProperties.class);
		var modules = domainModuleProperties.getModules();
		if (modules.isEmpty()) {
			return null;
		} else if (modules.size() == 1) {
			return modules.entrySet().stream().findAny().orElse(null);
		}
		
		var module = getModulePropertiesEntry(request, domainModuleProperties);
		
		if (module == null) {
			log.debug("The first module has been set because no module has been selected.");
			module = modules.entrySet().stream().findFirst().orElse(null);
		}
		return module;
	}
	
	protected abstract Entry<String, DomainProperties> getModulePropertiesEntry(HttpServletRequest request, DomainModuleProperties domainModuleProperties);
}
