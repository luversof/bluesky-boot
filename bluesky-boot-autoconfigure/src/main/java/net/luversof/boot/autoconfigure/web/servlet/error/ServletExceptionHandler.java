package net.luversof.boot.autoconfigure.web.servlet.error;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;
import net.luversof.boot.exception.BlueskyException;
import net.luversof.boot.exception.ErrorMessage;
import net.luversof.boot.exception.ErrorMessageInterface;
import net.luversof.boot.exception.ErrorPage;

/**
 * 공통 에러 처리 핸들러
 * @author bluesky
 *
 */
@Slf4j
@ConditionalOnWebApplication(type = Type.SERVLET)
@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class ServletExceptionHandler {
	
	@Resource(name = "messageSourceAccessor")
	private MessageSourceAccessor messageSourceAccessor;

	private DefaultMessageCodesResolver messageCodesResolver = new DefaultMessageCodesResolver();
	
	public static final String RESULT = "result";
	
	private static final String BRICK_EXCEPTION_MESSAGE = "[BrickException error message] code : {}";

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
			return handleApiBrickException(exception);
		}
		
		String[] errorCodes = getBrickExceptionErrorCodes(exception);
		log.debug(BRICK_EXCEPTION_MESSAGE, Arrays.deepToString(errorCodes));
		DefaultMessageSourceResolvable defaultMessageSourceResolvable = new DefaultMessageSourceResolvable(errorCodes, exception.getErrorMessageArgs(), exception.getMessage() == null ? exception.getErrorCode() : exception.getMessage());
    	String localizedMessage = messageSourceAccessor.getMessage(defaultMessageSourceResolvable);
		
		ErrorMessage errorMessage = new ErrorMessage();
		errorMessage.setErrorCode(exception.getErrorCode());
		errorMessage.setErrorMessageArgs(exception.getErrorMessageArgs());
		if (StringUtils.isEmpty(localizedMessage) || localizedMessage.equals(exception.getErrorCode())) {
			errorMessage.setMessage(exception.getMessage());
		} else {
			errorMessage.setMessage(localizedMessage);
			errorMessage.setDisplayableMessage(true);
		}
		errorMessage.setObject(exception.getErrorCode());
		errorMessage.setExceptionClassName(exception.getClass().getSimpleName());

		return getModelAndView(exception.getErrorPage(), errorMessage);
	}
	
	
	/**
	 * BrickException을 extends 한 Exception과 BrickException 간 errorCodes 를 반환하기 위해 구현
	 * 예를 들어 PreOrderException.preorder.preOrderRecord.NOT_EXIST_SCHEDULE 가 발생한 경우 결과는 아래와 같다.
	 * <ul>
	 * <li>PreOrderException.preorder.preOrderRecord.NOT_EXIST_SCHEDULE</li>
	 * <li>BrickException.preorder.preOrderRecord.NOT_EXIST_SCHEDULE</li>
	 * <li>PreOrderException</li>
	 * <li>BrickException</li>
	 * </ul>
	 * @param exception
	 * @return
	 */
	private String[] getBrickExceptionErrorCodes(BlueskyException exception) {
		String[] errorCodes = messageCodesResolver.resolveMessageCodes(exception.getClass().getSimpleName(), exception.getErrorCode());
		
		if (exception.getClass().isAssignableFrom(BlueskyException.class)) {
			return errorCodes;
		}
		String[] brickErrorCodes = messageCodesResolver.resolveMessageCodes(BlueskyException.class.getSimpleName(), exception.getErrorCode());
		List<String> errorCodeList = new ArrayList<>();
		errorCodeList.addAll(Arrays.asList(errorCodes));
		errorCodeList.addAll(Arrays.asList(brickErrorCodes));
		errorCodeList.sort((v1, v2) -> v1.chars().filter(e -> e == '.').count() > v2.chars().filter(e -> e == '.').count() ? -1 : 0 );
		return errorCodeList.toArray(new String[errorCodeList.size()]);
	}
	
	/**
	 * api 호출 에러 처리
	 * @param exception
	 * @return
	 */
	private ModelAndView handleApiBrickException(BlueskyException exception) {
		if (exception.getErrorMessage() != null) {
			ErrorMessage errorMessage = getErrorMessage(exception.getClass().getSimpleName(), exception.getErrorMessage());
			return getModelAndView(exception.getErrorPage(), errorMessage);
		}
		/**
		 * api 호출 반환시 에러 처리
		 */
		if (exception.getErrorMessageList() != null) {
			List<ErrorMessageInterface> errorMessageList = exception.getErrorMessageList();
			List<ErrorMessage> resultErrorMessageList = new ArrayList<>();
			for (ErrorMessageInterface errorMessage : errorMessageList) {
				ErrorMessage resultErrorMessage = getErrorMessage(exception.getClass().getSimpleName(), errorMessage);
				resultErrorMessageList.add(resultErrorMessage);
			}
			return getModelAndView(exception.getErrorPage(), resultErrorMessageList);
		}
		return null;
	}

	private ErrorMessage getErrorMessage(String exceptionName, ErrorMessageInterface errorMessage) {
		if (errorMessage instanceof ErrorMessage) {
			ErrorMessage targetErrorMessage = (ErrorMessage) errorMessage;
			// 로컬메세지 처리
			if (targetErrorMessage.getErrorCode() != null) {
				String errorCode = exceptionName + "." + targetErrorMessage.getErrorCode();
				log.debug(BRICK_EXCEPTION_MESSAGE, exceptionName + "." + targetErrorMessage.getErrorCode());
				targetErrorMessage.setMessage(messageSourceAccessor.getMessage(errorCode, targetErrorMessage.getErrorMessageArgs(), targetErrorMessage.getMessage() == null ? targetErrorMessage.getErrorCode() : targetErrorMessage.getMessage()));
			}
			return targetErrorMessage;
		}

		return new ErrorMessage();
	}

	/**
	 * hibernate validator 관련 BindException 처리
	 * @param exception
	 * @return
	 */
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ModelAndView handleException(BindException exception) {
		return getDefaultModelAndView(getErrorMessageList(exception.getClass().getSimpleName(), exception.getBindingResult()));
	}
	
	/**
	 * RequestBody 요청  관련 MethodArgumentNotValidException 처리
	 * @param exception
	 * @return
	 */
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ModelAndView handleException(MethodArgumentNotValidException exception) {
		return getDefaultModelAndView(getErrorMessageList(exception.getClass().getSimpleName(), exception.getBindingResult()));
	}
	
	private List<ErrorMessage> getErrorMessageList(String exceptionClassName, BindingResult bindingResult) {
		List<ErrorMessage> errorMessageList = new ArrayList<>();
		List<? extends ObjectError> objectErrorList = bindingResult.getFieldErrors().isEmpty() ? bindingResult.getAllErrors() : bindingResult.getFieldErrors();
		for (ObjectError objectError : objectErrorList) {
			ErrorMessage errorMessage = new ErrorMessage();
			errorMessage.setExceptionClassName(exceptionClassName);
			errorMessage.setMessage(messageSourceAccessor.getMessage(objectError));
			errorMessage.setObject(objectError.getObjectName());
			errorMessage.setDisplayableMessage(true);
			errorMessage.setErrorCode(objectError.getCode());
			if (objectError instanceof FieldError) {
				errorMessage.setField(((FieldError) objectError).getField());
			}
			errorMessageList.add(errorMessage);
			log.debug("[MethodArgumentNotValidException error message] code : {}, arguments : {}", Arrays.asList(objectError.getCodes()), Arrays.asList(objectError.getArguments()));
		}
		return errorMessageList;
	}
	
	/**
	 * 요청시 필요한 @RequestBody 가 없는 상태로 요청한 경우
	 * @param exception
	 * @return
	 */
	@ExceptionHandler
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ModelAndView handleException(HttpMessageNotReadableException exception) {
		log.debug("HttpMessageNotReadableException exception : ", exception);
		ErrorMessage errorMessage = new ErrorMessage();
		errorMessage.setMessage(messageSourceAccessor.getMessage(exception.getClass().getSimpleName()));
		errorMessage.setExceptionClassName(exception.getClass().getSimpleName());
		
		return getDefaultModelAndView(errorMessage);
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
		ErrorMessage errorMessage = new ErrorMessage();
		errorMessage.setMessage(exception.getMessage());
		errorMessage.setExceptionClassName(exception.getClass().getSimpleName());
		
		return getDefaultModelAndView(errorMessage);
	}
	
	
	private ModelAndView getModelAndView(String errorPage, ErrorMessage errorMessage) {
		Map<String, ErrorMessage> resultMap = new HashMap<>();
		resultMap.put(RESULT, errorMessage);
		return new ModelAndView(errorPage, resultMap);
	}
	
	private ModelAndView getModelAndView(String errorPage, List<ErrorMessage> errorMessageList) {
		Map<String, List<ErrorMessage>> resultMap = new HashMap<>();
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
