package net.luversof.boot.autoconfigure.security.exception.servlet;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.validation.DefaultMessageCodesResolver;
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
import net.luversof.boot.exception.BlueskyException;
import net.luversof.boot.exception.ErrorMessage;
import net.luversof.boot.exception.ErrorPage;

@Slf4j
@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE + 10)
public class SecurityExceptionHandler {
	
	private ContentNegotiatingViewResolver contentNegotiatingViewResolver;

	private DefaultMessageCodesResolver messageCodesResolver = new DefaultMessageCodesResolver();
	
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
		
		if (contentNegotiatingViewResolver.getContentNegotiationManager().resolveMediaTypes(request).contains(MediaType.APPLICATION_JSON)
				|| Arrays.asList(handlerMethod.getMethodAnnotation(RequestMapping.class).produces()).contains(MediaType.APPLICATION_JSON_VALUE)) {
			log.debug("json exception");
			Map<String, ErrorMessage> resultMap = new HashMap<>();
			resultMap.put(RESULT, getErrorMessage(exception));
			return new ModelAndView(ErrorPage.DEFAULT.getViewName(), resultMap);
		}
		
		throw exception;
	}
	
	

	private ErrorMessage getErrorMessage(Exception exception) {
		ErrorMessage errorMessage = new ErrorMessage();
		errorMessage.setExceptionClassName(exception.getClass().getSimpleName());
		
		if (exception instanceof BlueskyException) {
			String targetErrorCode = ((BlueskyException) exception).getErrorCode();
			//ERROR_CODE가 enum 값인 경우와 일반 String 인 경우를 구분지어야 함.
			String[] errorCodes = messageCodesResolver.resolveMessageCodes(exception.getClass().getSimpleName(), targetErrorCode);
			log.debug("[Exception error message] code : {}", Arrays.asList(errorCodes));
			DefaultMessageSourceResolvable defaultMessageSourceResolvable = new DefaultMessageSourceResolvable(errorCodes,  targetErrorCode);
			String localizedMessage = MessageUtil.getMessage(defaultMessageSourceResolvable);
			errorMessage.setMessage(localizedMessage);
			errorMessage.setDisplayableMessage(true);
        } else {
			errorMessage.setMessage(exception.getLocalizedMessage());
		}
		return errorMessage;
	}
}
