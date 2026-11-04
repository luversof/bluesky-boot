package io.github.luversof.boot.web.servlet.util;

import java.util.Collections;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import io.github.luversof.boot.context.ApplicationContextUtil;
import jakarta.servlet.http.HttpServletRequest;

public final class ServletRequestUtil {

  private static final Logger log = LoggerFactory.getLogger(ServletRequestUtil.class);

  private ServletRequestUtil() {}

  /**
   * url에 등록된 pathVariable을 조회하는 기능 일반적으로 interceptor 이후엔 requestAttribute에서 직접 꺼내 사용하면 되며
   * requestAttribute 값이 설정되기 이전인 filter에서 pathVariable을 사용할 수 있도록 제공함
   *
   * <p>핸들러 매핑이 핸들러를 찾으면서 {@link HandlerMapping#URI_TEMPLATE_VARIABLES_ATTRIBUTE} 에 실제로 맞은 패턴의 변수를
   * 남긴다. 그 값이 있으면 그대로 쓰고, 없으면(filter 단계) 매핑에 한 번 찾게 한 뒤 읽는다.
   *
   * @return
   */
  public static Map<String, String> getUriVariableMap() {
    var requestAttributes = RequestContextHolder.getRequestAttributes();
    if (requestAttributes == null) {
      return Collections.emptyMap();
    }
    HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

    var uriVariables = uriVariablesOf(request);
    if (uriVariables != null) {
      return uriVariables;
    }

    try {
      ApplicationContextUtil.getApplicationContext()
          .getBean("requestMappingHandlerMapping", RequestMappingHandlerMapping.class)
          .getHandler(request);
    } catch (Exception e) {
      log.debug("getUriVariableMap exception", e);
    }
    uriVariables = uriVariablesOf(request);
    return uriVariables == null ? Collections.emptyMap() : uriVariables;
  }

  @SuppressWarnings("unchecked")
  private static Map<String, String> uriVariablesOf(HttpServletRequest request) {
    return (Map<String, String>)
        request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
  }
}
