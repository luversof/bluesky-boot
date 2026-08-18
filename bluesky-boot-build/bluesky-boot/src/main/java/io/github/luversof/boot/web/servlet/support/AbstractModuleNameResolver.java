package io.github.luversof.boot.web.servlet.support;

import java.util.Map.Entry;

import io.github.luversof.boot.context.ApplicationContextUtil;
import io.github.luversof.boot.web.DomainModuleProperties;
import io.github.luversof.boot.web.DomainProperties;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Abstract classes of DomainModuleNameResolver, DomainAddPathPatternModuleNameResolver, and
 * AddPathPatternModuleNameResolver
 */
public abstract class AbstractModuleNameResolver implements ModuleNameResolver {

  @Override
  public String resolve(HttpServletRequest request) {
    var modulePropertiesEntry = getModulePropertiesEntry(request);
    return modulePropertiesEntry == null ? null : modulePropertiesEntry.getKey();
  }

  /**
   * Find module Entry matching the current request
   *
   * @param request HttpServletRequest
   * @return Entry of DomainProperties
   */
  private Entry<String, DomainProperties> getModulePropertiesEntry(HttpServletRequest request) {
    var domainModuleProperties =
        ApplicationContextUtil.getApplicationContext().getBean(DomainModuleProperties.class);
    var modules = domainModuleProperties.getModules();
    if (modules.isEmpty()) {
      return null;
    } else if (modules.size() == 1) {
      return modules.entrySet().stream().findAny().orElse(null);
    }

    return getModulePropertiesEntry(request, domainModuleProperties);
  }

  protected abstract Entry<String, DomainProperties> getModulePropertiesEntry(
      HttpServletRequest request, DomainModuleProperties domainModuleProperties);
}
