package io.github.luversof.boot.autoconfigure.exception.util;

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

import io.github.luversof.boot.util.ApplicationContextUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ExceptionUtil {
	
	@Setter
	private static List<ErrorViewResolver> errorViewResolverList;
	
	private static List<ErrorViewResolver> getErrorViewResolverList() {
		if (errorViewResolverList == null) {
			errorViewResolverList = ApplicationContextUtil.getApplicationContext().getBeanProvider(ErrorViewResolver.class).orderedStream().toList();
		}
		return errorViewResolverList;
	}
	
	public static Object handleException(ProblemDetail problemDetail, HandlerMethod handlerMethod, NativeWebRequest nativeWebRequest) {
		if (ExceptionUtil.isJsonResponse(handlerMethod, nativeWebRequest)) {
			return problemDetail;
		} else {
			for (ErrorViewResolver resolver : getErrorViewResolverList()) {
				HttpServletRequest httpServletRequest = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
				ModelAndView modelAndView = resolver.resolveErrorView(httpServletRequest, HttpStatus.valueOf(problemDetail.getStatus()), null);
				if (modelAndView != null) {
					return modelAndView;
				}
			}
			return null;
		}
	}

	public static boolean isHtmlResponse(HandlerMethod handlerMethod, NativeWebRequest request) {
		return !isJsonResponse(handlerMethod, request);
	}
	
	@SneakyThrows
	public static boolean isJsonResponse(HandlerMethod handlerMethod, NativeWebRequest request) {

		var contentNegotiationManager = ApplicationContextUtil.getApplicationContext().getBean(ContentNegotiationManager.class);
		if (contentNegotiationManager.resolveMediaTypes(request).contains(MediaType.APPLICATION_JSON)) {
			return true;
		}
		
		if (handlerMethod == null) {
			return false;
		}
		
		var methodAnnotation = handlerMethod.getMethodAnnotation(RequestMapping.class);
		if (methodAnnotation != null && Arrays.asList(methodAnnotation.produces()).contains(MediaType.APPLICATION_JSON_VALUE)) {
			return true;
		}
		
		var classAnnotation = handlerMethod.getMethod().getDeclaringClass().getAnnotation(RequestMapping.class);
		if (classAnnotation != null && Arrays.asList(classAnnotation.produces()).contains(MediaType.APPLICATION_JSON_VALUE)) {
			return true;
		}
		
		return false;
	}
}
