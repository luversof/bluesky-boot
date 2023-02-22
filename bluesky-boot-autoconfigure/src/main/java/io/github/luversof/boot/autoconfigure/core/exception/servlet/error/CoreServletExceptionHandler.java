package io.github.luversof.boot.autoconfigure.core.exception.servlet.error;

import java.util.ArrayList;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import io.github.luversof.boot.autoconfigure.context.MessageUtil;
import io.github.luversof.boot.exception.BlueskyException;
import lombok.extern.slf4j.Slf4j;

/**
 * 공통 에러 처리 핸들러
 * BlueskyErrorMessage로 반환하여 리턴하는 전제로 만들어졌음
 * @author bluesky
 *
 */
@Slf4j
@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class CoreServletExceptionHandler {
	
	private MessageSourceAccessor messageSourceAccessor;
	
	public CoreServletExceptionHandler(MessageSourceAccessor messageSourceAccessor) {
		this.messageSourceAccessor = messageSourceAccessor;
	}
	
	public static final String RESULT = "result";
	
	/**
	 * 프로젝트 공통 Exception 처리
	 * @param exception
	 * @return
	 * @throws HttpMediaTypeNotAcceptableException 
	 */
	@ExceptionHandler
	public ProblemDetail handleException(BlueskyException exception) {
		log.error("BlueskyException exception", exception);
		return MessageUtil.getProblemDetail(HttpStatus.BAD_REQUEST, exception);
	}
	
//	/**
//	 * api 호출 에러 처리
//	 * @param exception
//	 * @return
//	 */
//	private ModelAndView handleApiBlueskyException(BlueskyException exception, HandlerMethod handlerMethod, NativeWebRequest request) {
//		if (exception.getErrorMessage() != null) {
//			return getModelAndView(handlerMethod, request, exception.getErrorPage(), MessageUtil.getErrorMessageFromErrorMessage(exception));
//		}
//		if (exception.getErrorMessageList() != null) {
//			return getModelAndView(handlerMethod, request, exception.getErrorPage(), MessageUtil.getErrorMessageListFromErrorMessageList(exception));
//		}
//		return null;
//	}

//	/**
//	 * hibernate validator 관련 BindException 처리
//	 * @param exception
//	 * @return
//	 */
//	@ExceptionHandler
//	@ResponseStatus(HttpStatus.BAD_REQUEST)
//	public ModelAndView handleException(BindException exception, HandlerMethod handlerMethod, NativeWebRequest request) {
//		return getModelAndView(handlerMethod, request, MessageUtil.getErrorMessageList(exception));
//	}
	
	/**
	 * RequestBody 요청  관련 MethodArgumentNotValidException 처리
	 * @param exception
	 * @return
	 */
	@ExceptionHandler
	public ProblemDetail handleException(MethodArgumentNotValidException exception) {
		log.error("MethodArgumentNotValidException exception", exception);
		var problemDetail = exception.getBody();
		var allErrors = exception.getAllErrors();
		var errors = new ArrayList<ObjectError>();
		for (var error : allErrors) {
			var messageSourceResolvable = new DefaultMessageSourceResolvable(error.getCodes(), error.getArguments(), error.getDefaultMessage());
			var localizedMessage = messageSourceAccessor.getMessage(messageSourceResolvable);
			errors.add(new ObjectError(error.getObjectName(), error.getCodes(), error.getArguments(), localizedMessage));
		}
		
		problemDetail.setProperty("errors", errors);
		return problemDetail;
	}
	
	
	
//	/**
//	 * 요청시 필요한 @RequestBody 가 없는 상태로 요청한 경우
//	 * @param exception
//	 * @return
//	 */
//	@ExceptionHandler
//	@ResponseStatus(HttpStatus.BAD_REQUEST)
//	public ModelAndView handleException(HttpMessageNotReadableException exception, HandlerMethod handlerMethod, NativeWebRequest request) {
//		return getModelAndView(handlerMethod, request, MessageUtil.getProblemDetail(exception));
//	}
//	
	/**
	 * General Exception 처리
	 * @param exception
	 * @return
	 * @throws Throwable 
	 */
	@ExceptionHandler
	public ProblemDetail handleException(Throwable exception) {
		log.error("Throwable exception", exception);
		return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
	}
	
	
}
