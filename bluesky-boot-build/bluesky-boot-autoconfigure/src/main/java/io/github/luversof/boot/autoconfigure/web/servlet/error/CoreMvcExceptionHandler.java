package io.github.luversof.boot.autoconfigure.web.servlet.error;

import java.util.ArrayList;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import io.github.luversof.boot.autoconfigure.web.util.ExceptionUtil;
import io.github.luversof.boot.context.ApplicationContextUtil;
import io.github.luversof.boot.exception.BlueskyException;
import io.github.luversof.boot.web.util.ProblemDetailUtil;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.http.HttpServletRequest;

/**
 * servlet common error handling handlers
 * @author bluesky
 *
 */
@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class CoreMvcExceptionHandler {
	
	/**
	 * 프로젝트 공통 Exception 처리
	 * @param exception Thrown exception
	 * @return the created {@code ProblemDetail} instance
	 */
	@ExceptionHandler
	public <T extends BlueskyException> Object handleException(T exception, HandlerMethod handlerMethod, NativeWebRequest nativeWebRequest) {
		return ExceptionUtil.handleException(ProblemDetailUtil.getProblemDetail(exception), handlerMethod, nativeWebRequest);
	}
	
	@ExceptionHandler
	public <T extends BindException> Object handleException(T exception, HandlerMethod handlerMethod, NativeWebRequest nativeWebRequest) {
		return ExceptionUtil.handleException(ProblemDetailUtil.getProblemDetail(exception), handlerMethod, nativeWebRequest);
	}
	
	@ExceptionHandler
	public <T extends Exception> Object handleException(T exception, NativeWebRequest nativeWebRequest, ServletRequest servletRequest) {
		
		var requestMappingHandlerMappingMap = ApplicationContextUtil.getApplicationContext().getBeansOfType(RequestMappingHandlerMapping.class);
		var httpServletRequest = nativeWebRequest.getNativeRequest(HttpServletRequest.class);
		
		var handlerList = new ArrayList<>();
		requestMappingHandlerMappingMap.values().stream().forEach(x -> {
			HandlerExecutionChain handlerExecutionChain = null;
			try {
				handlerExecutionChain = x.getHandler(httpServletRequest);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (handlerExecutionChain != null) {
				handlerList.add(handlerExecutionChain.getHandler());
			}
		});
		return ExceptionUtil.handleException(ProblemDetailUtil.getProblemDetail(exception), handlerList.isEmpty() ? null : handlerList.get(0), nativeWebRequest);
	}
	
}
