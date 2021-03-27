package io.github.luversof.boot.autoconfigure.core.exception.servlet.error;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import io.github.luversof.boot.autoconfigure.context.MessageUtil;
import io.github.luversof.boot.exception.BlueskyException;
import io.github.luversof.boot.exception.BlueskyExceptionHandler;

/**
 * 공통 에러 처리 핸들러
 * BlueskyErrorMessage로 반환하여 리턴하는 전제로 만들어졌음
 * @author bluesky
 *
 */
@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class CoreServletExceptionHandler extends BlueskyExceptionHandler {
	
	public static final String RESULT = "result";
	
	/**
	 * 프로젝트 공통 Exception 처리
	 * @param exception
	 * @return
	 * @throws HttpMediaTypeNotAcceptableException 
	 */
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ModelAndView handleException(BlueskyException exception, HandlerMethod handlerMethod, NativeWebRequest request) {
		/**
		 * api 호출 반환시 에러 처리
		 */
		if (exception.getErrorMessage() != null || exception.getErrorMessageList() != null) {
			return handleApiBlueskyException(exception, handlerMethod, request);
		}
		
		return getModelAndView(handlerMethod, request, exception.getErrorPage(), MessageUtil.getErrorMessage(exception));
	}
	
	
	/**
	 * api 호출 에러 처리
	 * @param exception
	 * @return
	 */
	private ModelAndView handleApiBlueskyException(BlueskyException exception, HandlerMethod handlerMethod, NativeWebRequest request) {
		if (exception.getErrorMessage() != null) {
			return getModelAndView(handlerMethod, request, exception.getErrorPage(), MessageUtil.getErrorMessageFromErrorMessage(exception));
		}
		if (exception.getErrorMessageList() != null) {
			return getModelAndView(handlerMethod, request, exception.getErrorPage(), MessageUtil.getErrorMessageListFromErrorMessageList(exception));
		}
		return null;
	}

	/**
	 * hibernate validator 관련 BindException 처리
	 * @param exception
	 * @return
	 */
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ModelAndView handleException(BindException exception, HandlerMethod handlerMethod, NativeWebRequest request) {
		return getModelAndView(handlerMethod, request, MessageUtil.getErrorMessageList(exception));
	}
	
	/**
	 * RequestBody 요청  관련 MethodArgumentNotValidException 처리
	 * @param exception
	 * @return
	 * @throws HttpMediaTypeNotAcceptableException 
	 */
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ModelAndView handleException(MethodArgumentNotValidException exception, HandlerMethod handlerMethod, NativeWebRequest request) throws HttpMediaTypeNotAcceptableException {
		return getModelAndView(handlerMethod, request, MessageUtil.getErrorMessageList(exception));
	}
	
	/**
	 * 요청시 필요한 @RequestBody 가 없는 상태로 요청한 경우
	 * @param exception
	 * @return
	 */
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ModelAndView handleException(HttpMessageNotReadableException exception, HandlerMethod handlerMethod, NativeWebRequest request) {
		return getModelAndView(handlerMethod, request, MessageUtil.getErrorMessage(exception));
	}
	
	/**
	 * General Exception 처리
	 * @param exception
	 * @return
	 * @throws Throwable 
	 */
	@ExceptionHandler
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handleException(Throwable exception, HandlerMethod handlerMethod, NativeWebRequest request) {
		return getModelAndView(handlerMethod, request, MessageUtil.getErrorMessage(exception));
	}
	
	
}
