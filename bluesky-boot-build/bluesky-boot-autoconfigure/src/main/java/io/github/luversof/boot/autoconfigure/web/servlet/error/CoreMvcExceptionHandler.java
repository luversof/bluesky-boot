package io.github.luversof.boot.autoconfigure.web.servlet.error;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.HandlerMethod;

import io.github.luversof.boot.autoconfigure.web.util.ExceptionUtil;
import io.github.luversof.boot.exception.BlueskyException;
import io.github.luversof.boot.web.util.ProblemDetailUtil;

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
	public <T extends Throwable> Object handleException(T exception, HandlerMethod handlerMethod, NativeWebRequest nativeWebRequest) {
		return ExceptionUtil.handleException(ProblemDetailUtil.getProblemDetail(exception), handlerMethod, nativeWebRequest);
	}
	
}
