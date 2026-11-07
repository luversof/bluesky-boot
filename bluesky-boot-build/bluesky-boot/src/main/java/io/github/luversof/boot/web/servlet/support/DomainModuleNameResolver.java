package io.github.luversof.boot.web.servlet.support;

import java.net.URI;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Stream;

import org.springframework.util.StringUtils;

import io.github.luversof.boot.web.DomainModuleProperties;
import io.github.luversof.boot.web.DomainProperties;
import jakarta.servlet.http.HttpServletRequest;

/** Resolver that resolves moduleName based on domain */
public class DomainModuleNameResolver extends AbstractModuleNameResolver {

  public DomainModuleNameResolver(DomainModuleProperties domainModuleProperties) {
    super(domainModuleProperties);
  }

  @Override
  protected Entry<String, DomainProperties> getModulePropertiesEntry(
      HttpServletRequest request, DomainModuleProperties domainModuleProperties) {
    // 해당 도메인에 해당하는 모듈 entry list 확인
    var moduleEntryList =
        domainModuleProperties.getModules().entrySet().stream()
            .filter(moduleEntry -> checkDomain(request, moduleEntry.getValue()))
            .toList();

    if (moduleEntryList.isEmpty()) {
      return null;
    }

    if (moduleEntryList.size() == 1) {
      return moduleEntryList.get(0);
    }

    // 2개 이상이면 요청 경로에 맞는 URL 경로가 가장 긴 모듈을 고른다(같으면 먼저 선언된 모듈)
    return moduleEntryList.stream()
        .filter(moduleEntry -> matchedPathLength(request, moduleEntry.getValue()) >= 0)
        .max(
            Comparator.comparingInt(
                moduleEntry -> matchedPathLength(request, moduleEntry.getValue())))
        .orElse(null);
  }

  private boolean checkDomain(HttpServletRequest request, DomainProperties domainProperties) {
    if (request == null) {
      return false;
    }

    return domainProperties != null
        && (checkDomain(request, domainProperties.getWebUrls())
            || checkDomain(request, domainProperties.getMobileWebUrls())
            || checkDomain(request, domainProperties.getDevDomains()));
  }

  private boolean checkDomain(HttpServletRequest request, List<URI> uriList) {
    if (request == null) {
      return false;
    }
    return uriList.stream().anyMatch(uri -> uri.getHost().equals(request.getServerName()));
  }

  /**
   * 같은 호스트의 URL 중 요청 경로에 경로 경계까지 맞는 것의 경로 길이(가장 긴 것), 없으면 -1.
   *
   * <p>"/shop" 은 "/shop", "/shop/item" 에만 맞고 "/shopping" 에는 맞지 않는다.
   */
  private int matchedPathLength(HttpServletRequest request, DomainProperties domainProperties) {
    var servletPath = request.getServletPath();
    return Stream.of(
            domainProperties.getWebUrls(),
            domainProperties.getMobileWebUrls(),
            domainProperties.getDevDomains())
        .flatMap(List::stream)
        .filter(uri -> uri.getHost().equals(request.getServerName()))
        .map(uri -> StringUtils.trimTrailingCharacter(uri.getPath(), '/'))
        .filter(path -> servletPath.equals(path) || servletPath.startsWith(path + "/"))
        .mapToInt(String::length)
        .max()
        .orElse(-1);
  }
}
