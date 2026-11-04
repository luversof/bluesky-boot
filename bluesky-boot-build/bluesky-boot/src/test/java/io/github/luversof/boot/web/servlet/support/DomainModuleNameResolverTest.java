package io.github.luversof.boot.web.servlet.support;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
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
}
