package io.github.luversof.boot.web.servlet.support;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import io.github.luversof.boot.web.DomainModuleProperties;
import io.github.luversof.boot.web.DomainProperties;
import jakarta.servlet.http.HttpServletRequest;

/** A resolver that resolves moduleName based on a combination of Domain and AddPathPattern. */
public class DomainAddPathPatternModuleNameResolver extends AbstractModuleNameResolver {

  private final PathMatcher pathMatcher = new AntPathMatcher();
  private final Comparator<Entry<String, DomainProperties>> addPathPatternComparator =
      (o1, o2) ->
          Integer.compare(
              o1.getValue().getAddPathPatterns().get(0).length(),
              o2.getValue().getAddPathPatterns().get(0).length());

  public DomainAddPathPatternModuleNameResolver(DomainModuleProperties domainModuleProperties) {
    super(domainModuleProperties);
  }

  @Override
  protected Entry<String, DomainProperties> getModulePropertiesEntry(
      HttpServletRequest request, DomainModuleProperties domainModuleProperties) {
    // 도메인으로 먼저 거르고, 2개 이상 남으면 addPathPattern 으로 하나를 고른다
    var moduleEntryList =
        domainModuleProperties.getModules().entrySet().stream()
            .filter(moduleEntry ->
                DomainModuleNameResolver.checkDomain(request, moduleEntry.getValue()))
            .toList();

    if (moduleEntryList.size() > 1) {
      moduleEntryList = getEntryListFilterByAddPathPattern(request, moduleEntryList);
    }

    // addPathPattern은 단일 또는 빈 목록을 반환하므로 2개 이상이 있을 가능성은 없음
    return moduleEntryList.size() == 1 ? moduleEntryList.get(0) : null;
  }

  private List<Entry<String, DomainProperties>> getEntryListFilterByAddPathPattern(
      HttpServletRequest request, List<Entry<String, DomainProperties>> moduleEntryList) {
    var moduleEntry =
        moduleEntryList.stream()
            .filter(entry ->
                entry.getValue().getAddPathPatterns().stream()
                    .anyMatch(addPathPattern ->
                        pathMatcher.match(addPathPattern, request.getServletPath())))
            .sorted(addPathPatternComparator.reversed())
            .findFirst()
            .orElse(null);
    return moduleEntry == null ? Collections.emptyList() : List.of(moduleEntry);
  }
}
