package net.luversof.boot.autoconfigure.core.exception.servlet.error;

import java.util.HashMap;
import java.util.List;

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
import org.springframework.web.servlet.ModelAndView;

import net.luversof.boot.autoconfigure.context.MessageUtil;
import net.luversof.boot.exception.BlueskyException;
import net.luversof.boot.exception.ErrorMessage;
import net.luversof.boot.exception.ErrorPage;

/**
 * 공통 에러 처리 핸들러
 * @author bluesky
 *
 */
@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class CoreServletExceptionHandler {
	
	public static final String RESULT = "result";

	/**
	 * 프로젝트 공통 Exception 처리
	 * @param exception
	 * @return
	 * @throws HttpMediaTypeNotAcceptableException 
	 */
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ModelAndView handleException(BlueskyException exception) {
		/**
		 * api 호출 반환시 에러 처리
		 */
		if (exception.getErrorMessage() != null || exception.getErrorMessageList() != null) {
			return handleApiBlueskyException(exception);
		}
		
		return getModelAndView(exception.getErrorPage(), MessageUtil.getErrorMessage(exception));
	}
	
	
	/**
	 * api 호출 에러 처리
	 * @param exception
	 * @return
	 */
	private ModelAndView handleApiBlueskyException(BlueskyException exception) {
		if (exception.getErrorMessage() != null) {
			return getModelAndView(exception.getErrorPage(), MessageUtil.getErrorMessageFromErrorMessage(exception));
		}
		if (exception.getErrorMessageList() != null) {
			return getModelAndView(exception.getErrorPage(), MessageUtil.getErrorMessageListFromErrorMessageList(exception));
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
	public ModelAndView handleException(BindException exception) {
		return getDefaultModelAndView(MessageUtil.getErrorMessageList(exception));
	}
	
	/**
	 * RequestBody 요청  관련 MethodArgumentNotValidException 처리
	 * @param exception
	 * @return
	 */
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ModelAndView handleException(MethodArgumentNotValidException exception) {
		return getDefaultModelAndView(MessageUtil.getErrorMessageList(exception));
	}
	
	/**
	 * 요청시 필요한 @RequestBody 가 없는 상태로 요청한 경우
	 * @param exception
	 * @return
	 */
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ModelAndView handleException(HttpMessageNotReadableException exception) {
		return getDefaultModelAndView(MessageUtil.getErrorMessage(exception));
	}
	
	/**
	 * General Exception 처리
	 * @param exception
	 * @return
	 * @throws Throwable 
	 */
	@ExceptionHandler
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ModelAndView handleException(Throwable exception) {
		return getDefaultModelAndView(MessageUtil.getErrorMessage(exception));
	}
	
	
	private ModelAndView getModelAndView(String errorPage, ErrorMessage errorMessage) {
		var resultMap = new HashMap<String, ErrorMessage>();
		resultMap.put(RESULT, errorMessage);
		return new ModelAndView(errorPage, resultMap);
	}
	
	private ModelAndView getModelAndView(String errorPage, List<ErrorMessage> errorMessageList) {
		var resultMap = new HashMap<String, List<ErrorMessage>>();
		resultMap.put(RESULT, errorMessageList);
		return new ModelAndView(errorPage, resultMap);
	}
	
	private ModelAndView getDefaultModelAndView(ErrorMessage errorMessage) {
		return getModelAndView(ErrorPage.DEFAULT.getViewName(), errorMessage);
	}
	
	private ModelAndView getDefaultModelAndView(List<ErrorMessage> errorMessageList) {
		return getModelAndView(ErrorPage.DEFAULT.getViewName(), errorMessageList);
	}
}
