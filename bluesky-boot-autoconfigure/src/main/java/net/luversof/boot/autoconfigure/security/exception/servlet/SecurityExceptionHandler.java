package net.luversof.boot.autoconfigure.security.exception.servlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

import javax.servlet.http.HttpServletResponse;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.luversof.boot.autoconfigure.context.MessageUtil;
import net.luversof.boot.exception.BlueskyErrorPage;
import net.luversof.boot.exception.ErrorMessage;

@Slf4j
@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE + 10)
public class SecurityExceptionHandler {
	
	private ContentNegotiatingViewResolver contentNegotiatingViewResolver;

	public static final String RESULT = "result";

	public SecurityExceptionHandler(ContentNegotiatingViewResolver contentNegotiatingViewResolver) {
		this.contentNegotiatingViewResolver = contentNegotiatingViewResolver;
	}

	@ExceptionHandler
	@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
	public ModelAndView preAuthenticatedCredentialsNotFoundException(PreAuthenticatedCredentialsNotFoundException exception) {
		return new ModelAndView("login");
	}

	@SneakyThrows
	@ExceptionHandler
	@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
	public ModelAndView accessDeniedException(HttpServletResponse response, AccessDeniedException exception, HandlerMethod  handlerMethod, NativeWebRequest request) throws IOException {
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
		
		//json html return 분기 처리
		if (!isJsonResponse) {
			throw exception;
		}
		
		log.debug("json exception");
		var resultMap = new HashMap<String, ErrorMessage>();
		resultMap.put(RESULT, MessageUtil.getErrorMessage(exception));
		return new ModelAndView(BlueskyErrorPage.DEFAULT.getViewName(), resultMap);

	}
}
