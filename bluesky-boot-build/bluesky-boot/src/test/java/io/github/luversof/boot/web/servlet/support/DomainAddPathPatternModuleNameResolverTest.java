package io.github.luversof.boot.web.servlet.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import io.github.luversof.boot.web.DomainModuleProperties;
import io.github.luversof.boot.web.DomainProperties;

/** 도메인으로 먼저 거르고, 같은 도메인에 모듈이 여럿이면 가장 긴 addPathPattern 이 맞는 모듈을 고른다. */
class DomainAddPathPatternModuleNameResolverTest {

  private final DomainAddPathPatternModuleNameResolver resolver;

  DomainAddPathPatternModuleNameResolverTest() {
    var modules = new LinkedHashMap<String, DomainProperties>();
    modules.put("main", domain("https://a.com", "/**"));
    modules.put("shop", domain("https://a.com", "/shop/**"));
    modules.put("admin", domain("https://admin.com", "/**"));
    var domainModuleProperties = new DomainModuleProperties();
    domainModuleProperties.setModules(modules);
    resolver = new DomainAddPathPatternModuleNameResolver(domainModuleProperties);
  }

  @Test
  void singleDomainMatchWins() {
    assertThat(resolver.resolve(request("admin.com", "/shop/item"))).isEqualTo("admin");
  }

  @Test
  void longestPathPatternWinsOnSameDomain() {
    assertThat(resolver.resolve(request("a.com", "/shop/item"))).isEqualTo("shop");
    assertThat(resolver.resolve(request("a.com", "/board"))).isEqualTo("main");
  }

  @Test
  void unknownDomainResolvesNothing() {
    assertThat(resolver.resolve(request("other.com", "/"))).isNull();
  }

  private static DomainProperties domain(String webUrl, String addPathPattern) {
    var domainProperties = new DomainProperties();
    domainProperties.setWebUrls(List.of(URI.create(webUrl)));
    domainProperties.setMobileWebUrls(List.of());
    domainProperties.setDevDomains(List.of());
    domainProperties.setAddPathPatterns(List.of(addPathPattern));
    return domainProperties;
  }

  private static MockHttpServletRequest request(String serverName, String servletPath) {
    var request = new MockHttpServletRequest();
    request.setServerName(serverName);
    request.setServletPath(servletPath);
    return request;
  }
}
