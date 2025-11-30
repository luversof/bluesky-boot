package io.github.luversof.boot.autoconfigure.web.util;

import java.util.Arrays;
import java.util.List;

import org.springframework.boot.autoconfigure.web.servlet.error.ErrorViewResolver;
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
import lombok.Setter;
import lombok.SneakyThrows;

/**
 * Utility for handling responses to exceptions
 */
public final class ExceptionUtil {
	
	/**
	 * Handling utility class constructors
	 */
	private ExceptionUtil() {
	}
	
	@Setter
	private static List<ErrorViewResolver> errorViewResolverList;
	
	private static List<ErrorViewResolver> getErrorViewResolverList() {
		if (errorViewResolverList == null) {
			errorViewResolverList = ApplicationContextUtil.getApplicationContext().getBeanProvider(ErrorViewResolver.class).orderedStream().toList();
		}
		return errorViewResolverList;
	}
	
	/**
	 * If the response is json, return a ProblemDetail object, otherwise return a ModelAndView object using ErrorViewResolver.
	 * 
	 * @param problemDetail problemDetail
	 * @param handler handler
	 * @param nativeWebRequest nativeWebRequest
	 * @return Returns a modelAndView or problemDetail object depending on the situation.
	 */
	public static Object handleException(ProblemDetail problemDetail, Object handler, NativeWebRequest nativeWebRequest) {
		if (ExceptionUtil.isJsonResponse(handler, nativeWebRequest)) {
			return problemDetail;
		} else {
			for (ErrorViewResolver resolver : getErrorViewResolverList()) {
				HttpServletRequest httpServletRequest = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
				ModelAndView modelAndView = resolver.resolveErrorView(httpServletRequest, HttpStatus.valueOf(problemDetail.getStatus()), problemDetail.getProperties());
				if (modelAndView != null) {
					return modelAndView;
				}
			}
			return null;
		}
	}

	/**
	 * Returns whether the request should be processed as a json response.
	 * 
	 * @param handler handler
	 * @param request request
	 * @return isJsonResponse
	 */
	@SneakyThrows
	private static boolean isJsonResponse(Object handler, NativeWebRequest request) {

		var contentNegotiationManager = ApplicationContextUtil.getApplicationContext().getBean(ContentNegotiationManager.class);
		if (contentNegotiationManager.resolveMediaTypes(request).contains(MediaType.APPLICATION_JSON)) {
			return true;
		}
		
		if (!(handler instanceof HandlerMethod)) {
			return false;
		}
		var handlerMethod = (HandlerMethod) handler;
		
		var methodAnnotation = handlerMethod.getMethodAnnotation(RequestMapping.class);
		if (methodAnnotation != null && Arrays.asList(methodAnnotation.produces()).contains(MediaType.APPLICATION_JSON_VALUE)) {
			return true;
		}
		
		var classAnnotation = handlerMethod.getMethod().getDeclaringClass().getAnnotation(RequestMapping.class);
		return classAnnotation != null && Arrays.asList(classAnnotation.produces()).contains(MediaType.APPLICATION_JSON_VALUE);
	}
}
