package io.github.luversof.boot.autoconfigure.web.servlet.error;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

import io.github.luversof.boot.autoconfigure.web.util.ExceptionUtil;
import io.github.luversof.boot.exception.BlueskyException;
import io.github.luversof.boot.web.util.ProblemDetailUtil;

/**
 * servlet common error handling handlers
 *
 * @author bluesky
 */
@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class CoreMvcExceptionHandler {

  /**
   * BlueskyException handling
   *
   * @param <T> BlueskyException extension type
   * @param exception exception
   * @param handlerMethod handlerMethod
   * @param nativeWebRequest nativeWebRequest
   * @return Returns a modelAndView or problemDetail object depending on the situation.
   */
  @ExceptionHandler
  public <T extends BlueskyException> Object handleException(
      T exception, HandlerMethod handlerMethod, NativeWebRequest nativeWebRequest) {
    return ExceptionUtil.handleException(
        ProblemDetailUtil.getProblemDetail(exception), handlerMethod, nativeWebRequest);
  }

  /**
   * BindException handling
   *
   * @param <T> BindException extension type
   * @param exception exception
   * @param handlerMethod handlerMethod
   * @param nativeWebRequest nativeWebRequest
   * @return Returns a modelAndView or problemDetail object depending on the situation.
   */
  @ExceptionHandler
  public <T extends BindException> Object handleException(
      T exception, HandlerMethod handlerMethod, NativeWebRequest nativeWebRequest) {
    return ExceptionUtil.handleException(
        ProblemDetailUtil.getProblemDetail(exception), handlerMethod, nativeWebRequest);
  }

  /**
   * Exception handling
   *
   * @param <T> Exception extension type
   * @param exception exception
   * @param nativeWebRequest nativeWebRequest
   * @return Returns a modelAndView or problemDetail object depending on the situation.
   */
  @ExceptionHandler
  public <T extends Exception> Object handleException(
      T exception, NativeWebRequest nativeWebRequest) {
    // 응답 형식(화면/JSON)을 고를 핸들러는 매핑이 찾을 때 요청에 남겨 둔 것을 쓴다. 예전에는 예외마다 매핑 전체로 다시 찾았다.
    // 405 처럼 핸들러를 못 찾은 요청이면 null 이다(예전에도 다시 찾다 실패해 null 이었다).
    var handler =
        nativeWebRequest.getAttribute(
            HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
    return ExceptionUtil.handleException(
        ProblemDetailUtil.getProblemDetail(exception), handler, nativeWebRequest);
  }
}
