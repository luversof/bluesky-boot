package io.github.luversof.boot.web.servlet.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import io.github.luversof.boot.web.DomainModuleProperties;
import io.github.luversof.boot.web.DomainProperties;

/** resolver 는 생성할 때 받은 DomainModuleProperties 를 본다(refresh 는 같은 인스턴스를 다시 채운다). */
class DomainModuleNameResolverTest {

  @Test
  void usesInjectedModulePropertiesIncludingLaterChanges() {
    var domainModuleProperties = new DomainModuleProperties();
    domainModuleProperties.setModules(new HashMap<>());
    var resolver = new DomainModuleNameResolver(domainModuleProperties);
    var request = new MockHttpServletRequest();

    assertThat(resolver.resolve(request)).isNull();

    domainModuleProperties.setModules(new HashMap<>(Map.of("only", new DomainProperties())));
    assertThat(resolver.resolve(request)).isEqualTo("only");
  }

  /** 같은 호스트에 모듈이 여럿이면 경로 경계까지 맞는 URL 중 경로가 가장 긴 모듈을 고른다 - 설정 순서와 무관해야 한다. */
  @Test
  void picksLongestMatchingPathOnSameHost() {
    var modules = new LinkedHashMap<String, DomainProperties>();
    modules.put("main", domain("https://a.com"));
    modules.put("shop", domain("https://a.com/shop"));
    var domainModuleProperties = new DomainModuleProperties();
    domainModuleProperties.setModules(modules);
    var resolver = new DomainModuleNameResolver(domainModuleProperties);

    assertThat(resolver.resolve(request("/shop/item"))).isEqualTo("shop");
    assertThat(resolver.resolve(request("/shop"))).isEqualTo("shop");
    assertThat(resolver.resolve(request("/shopping"))).isEqualTo("main");
    assertThat(resolver.resolve(request("/"))).isEqualTo("main");
  }

  private static DomainProperties domain(String webUrl) {
    var domainProperties = new DomainProperties();
    domainProperties.setWebUrls(List.of(URI.create(webUrl)));
    domainProperties.setMobileWebUrls(List.of());
    domainProperties.setDevDomains(List.of());
    return domainProperties;
  }

  private static MockHttpServletRequest request(String servletPath) {
    var request = new MockHttpServletRequest();
    request.setServerName("a.com");
    request.setServletPath(servletPath);
    return request;
  }
}
