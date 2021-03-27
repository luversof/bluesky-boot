package io.github.luversof.boot.exception;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;

import lombok.SneakyThrows;

public abstract class BlueskyExceptionHandler {
	
	public static final String RESULT = "result";
	
	public static final String JSON_VIEW = "jsonView";	// jsonView bean name
	
	@Autowired
	private ContentNegotiatingViewResolver contentNegotiatingViewResolver;
	
	@SneakyThrows
	protected boolean isJsonResponse(HandlerMethod handlerMethod, NativeWebRequest request) {
		var contentNegotiationManager = contentNegotiatingViewResolver.getContentNegotiationManager();
		var isJsonResponse = false;
		if (contentNegotiationManager != null) {
			isJsonResponse = contentNegotiationManager.resolveMediaTypes(request).contains(MediaType.APPLICATION_JSON);
		}
		
		var methodAnnotation = handlerMethod.getMethodAnnotation(RequestMapping.class);
		if (!isJsonResponse && methodAnnotation != null) {
			isJsonResponse = Arrays.asList(methodAnnotation.produces()).contains(MediaType.APPLICATION_JSON_VALUE);
		}
		
		var classAnnotation = handlerMethod.getMethod().getDeclaringClass().getAnnotation(RequestMapping.class);
		if (!isJsonResponse && classAnnotation != null) {
			isJsonResponse = Arrays.asList(classAnnotation.produces()).contains(MediaType.APPLICATION_JSON_VALUE);
		}
		return isJsonResponse;
	}
	
	protected ModelAndView getModelAndView(String viewName, BlueskyErrorMessage errorMessage) {
		var modelAndView = new ModelAndView(viewName);
		modelAndView.addObject(errorMessage);
		return modelAndView;
	}
	
	protected ModelAndView getModelAndView(String viewName, List<BlueskyErrorMessage> errorMessageList) {
		var modelAndView = new ModelAndView(viewName);
		modelAndView.addObject(errorMessageList);
		return modelAndView;
	}
	
	protected ModelAndView getModelAndView(HandlerMethod handlerMethod, NativeWebRequest request, String viewName, BlueskyErrorMessage errorMessage) {
		return getModelAndView(isJsonResponse(handlerMethod, request) ? JSON_VIEW : viewName, errorMessage);
	}
	
	protected ModelAndView getModelAndView(HandlerMethod handlerMethod, NativeWebRequest request, BlueskyErrorMessage errorMessage) {
		return getModelAndView(handlerMethod, request, BlueskyErrorPage.DEFAULT.getViewName(), errorMessage);
	}
	
	protected ModelAndView getModelAndView(HandlerMethod handlerMethod, NativeWebRequest request, String viewName, List<BlueskyErrorMessage> errorMessageList) {
		return getModelAndView(isJsonResponse(handlerMethod, request) ? JSON_VIEW : viewName, errorMessageList);
	}
	
	protected ModelAndView getModelAndView(HandlerMethod handlerMethod, NativeWebRequest request, List<BlueskyErrorMessage> errorMessageList) {
		return getModelAndView(handlerMethod, request, BlueskyErrorPage.DEFAULT.getViewName(), errorMessageList);
	}
}
