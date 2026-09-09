package io.github.luversof.boot.autoconfigure.web.util;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.webmvc.autoconfigure.error.ErrorViewResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.web.accept.ContentNegotiationManager;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import io.github.luversof.boot.context.ApplicationContextUtil;
import jakarta.servlet.http.HttpServletRequest;

/** Utility for handling responses to exceptions */
public final class ExceptionUtil {

  /** Handling utility class constructors */
  private ExceptionUtil() {}

  private static List<ErrorViewResolver> errorViewResolverList;

  public static void setErrorViewResolverList(List<ErrorViewResolver> errorViewResolverList) {
    ExceptionUtil.errorViewResolverList = errorViewResolverList;
  }

  private static List<ErrorViewResolver> getErrorViewResolverList() {
    if (errorViewResolverList == null) {
      errorViewResolverList =
          ApplicationContextUtil.getApplicationContext()
              .getBeanProvider(ErrorViewResolver.class)
              .orderedStream()
              .toList();
    }
    return errorViewResolverList;
  }

  /**
   * If the response is json, return a ProblemDetail object, otherwise return a ModelAndView object
   * using ErrorViewResolver.
   *
   * @param problemDetail problemDetail
   * @param handler handler
   * @param nativeWebRequest nativeWebRequest
   * @return Returns a modelAndView or problemDetail object depending on the situation.
   */
  public static Object handleException(
      ProblemDetail problemDetail, Object handler, NativeWebRequest nativeWebRequest) {
    if (ExceptionUtil.isJsonResponse(handler, nativeWebRequest)) {
      return problemDetail;
    } else {
      for (ErrorViewResolver resolver : getErrorViewResolverList()) {
        HttpServletRequest httpServletRequest =
            nativeWebRequest.getNativeRequest(HttpServletRequest.class);
        ModelAndView modelAndView =
            resolver.resolveErrorView(
                httpServletRequest,
                HttpStatus.valueOf(problemDetail.getStatus()),
                problemDetail.getProperties());
        if (modelAndView != null) {
          return modelAndView;
        }
      }
      // 그릴 오류 화면이 없으면(HTML 뷰가 없는 API 서비스, 또는 Accept: */*) ProblemDetail 로 떨어진다.
      // 예전에는 null 을 돌려줬는데 Spring 은 @ExceptionHandler 의 null 을 '처리 끝' 으로 보고 200 빈 응답을 보냈다
      // (실측 2026-09-09: api-stock 에 Accept 없이 400 예외를 내면 HTTP 200, Content-Length 0).
      return problemDetail;
    }
  }

  public static boolean isHtmlResponse(HandlerMethod handlerMethod, NativeWebRequest request) {
    return !isJsonResponse(handlerMethod, request);
  }

  /**
   * Returns whether the request should be processed as a json response.
   *
   * @param handler handler
   * @param request request
   * @return isJsonResponse
   */
  private static boolean isJsonResponse(Object handler, NativeWebRequest request) {
    try {
      var contentNegotiationManager =
          ApplicationContextUtil.getApplicationContext().getBean(ContentNegotiationManager.class);
      if (contentNegotiationManager
          .resolveMediaTypes(request)
          .contains(MediaType.APPLICATION_JSON)) {
        return true;
      }

      if (!(handler instanceof HandlerMethod)) {
        return false;
      }
      var handlerMethod = (HandlerMethod) handler;

      var methodAnnotation = handlerMethod.getMethodAnnotation(RequestMapping.class);
      if (methodAnnotation != null
          && Arrays.asList(methodAnnotation.produces())
              .contains(MediaType.APPLICATION_JSON_VALUE)) {
        return true;
      }

      var classAnnotation =
          handlerMethod.getMethod().getDeclaringClass().getAnnotation(RequestMapping.class);
      return classAnnotation != null
          && Arrays.asList(classAnnotation.produces()).contains(MediaType.APPLICATION_JSON_VALUE);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
