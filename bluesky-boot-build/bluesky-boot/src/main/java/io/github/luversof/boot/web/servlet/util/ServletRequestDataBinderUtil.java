package io.github.luversof.boot.web.servlet.util;

import java.io.IOException;
import java.io.UncheckedIOException;

import org.springframework.beans.BeanUtils;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.Assert;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.ServletRequestDataBinder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.ExtendedServletRequestDataBinder;

import io.github.luversof.boot.context.ApplicationContextUtil;
import io.github.luversof.boot.validation.ValidationUtil;
import jakarta.servlet.http.HttpServletRequest;
import tools.jackson.databind.json.JsonMapper;

/**
 * 요청받은 request 의 parameter를 기반으로 modelAttribute object를 호출하는 유틸. servletRequest를 기반으로 하기 때문에 되도록
 * controller 에서 사용해야함
 *
 * @author bluesky
 */
public final class ServletRequestDataBinderUtil {

  private ServletRequestDataBinderUtil() {}

  public static <T> T getObject(String objectName, Class<T> clazz, Object... validationHints) {
    var requestAttributes = RequestContextHolder.currentRequestAttributes();
    Assert.notNull(requestAttributes, "requestAttributes must exist");
    HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

    T instantiateClass = BeanUtils.instantiateClass(clazz);

    ServletRequestDataBinder servletRequestDataBinder =
        objectName == null
            ? new ExtendedServletRequestDataBinder(instantiateClass)
            : new ExtendedServletRequestDataBinder(instantiateClass, objectName);
    servletRequestDataBinder.setConversionService(
        ApplicationContextUtil.getApplicationContext().getBean(FormattingConversionService.class));
    servletRequestDataBinder.bind(request);

    @SuppressWarnings("unchecked")
    T target = (T) servletRequestDataBinder.getTarget();

    if (validationHints != null) {
      ValidationUtil.validate(target, validationHints);
    }

    return target;
  }

  public static <T> T getObject(Class<T> clazz) {
    return getObject(null, clazz, (Object[]) null);
  }

  public static <T> T getObject(String objectName, Class<T> clazz) {
    return getObject(objectName, clazz, (Object[]) null);
  }

  public static <T> T getObject(Class<T> clazz, Object... validationHints) {
    return getObject(null, clazz, validationHints);
  }

  public static <T> T getRequestBodyObject(
      JsonMapper jsonMapper, Class<T> clazz, Object... validationHints) {
    try {
      var request =
          ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
      var inputMessage = new ServletServerHttpRequest(request);
      var inputStream = StreamUtils.nonClosing(inputMessage.getBody());

      if (jsonMapper == null) {
        jsonMapper = ApplicationContextUtil.getApplicationContext().getBean(JsonMapper.class);
      }

      T target = jsonMapper.readValue(inputStream, clazz);

      if (validationHints != null) {
        ValidationUtil.validate(target, validationHints);
      }
      return target;
    } catch (IOException e) {
      // null 을 돌려주면 호출자는 검증된 객체로 믿고 넘긴다
      throw new UncheckedIOException("failed to read request body", e);
    }
  }

  public static <T> T getRequestBodyObject(Class<T> clazz, Object... validationHints) {
    return getRequestBodyObject(null, clazz, validationHints);
  }

  public static <T> T getRequestBodyObject(JsonMapper jsonMapper, Class<T> clazz) {
    return getRequestBodyObject(jsonMapper, clazz, (Object[]) null);
  }

  public static <T> T getRequestBodyObject(Class<T> clazz) {
    return getRequestBodyObject(clazz, (Object[]) null);
  }
}
