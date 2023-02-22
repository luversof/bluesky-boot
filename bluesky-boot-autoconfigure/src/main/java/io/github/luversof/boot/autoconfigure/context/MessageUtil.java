package io.github.luversof.boot.autoconfigure.context;

import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.DefaultMessageCodesResolver;

import io.github.luversof.boot.exception.BlueskyException;
import io.github.luversof.boot.exception.BlueskyProblemDetail;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MessageUtil {

	@Setter
	private static MessageSourceAccessor messageSourceAccessor;
	
	private static DefaultMessageCodesResolver messageCodesResolver = new DefaultMessageCodesResolver();
	
	public static String getMessage(String code, String defaultMessage) {
		if (messageSourceAccessor == null) {
			return defaultMessage;
		}
		return messageSourceAccessor.getMessage(code, defaultMessage);
	}
	
	public static String getMessage(String code) {
		return getMessage(code, "");
	}
	
	public static String getMessage(MessageSourceResolvable resolvable) {
		if (messageSourceAccessor == null) {
			return "";
		}
		return messageSourceAccessor.getMessage(resolvable);
	}
	
	public static BlueskyProblemDetail getProblemDetail(HttpStatus httpStatus, Throwable exception) {
		return getProblemDetail(httpStatus.value(), exception);
	}
	
	public static BlueskyProblemDetail getProblemDetail(int status, Throwable exception) {
		var problemDetail = BlueskyProblemDetail.forStatus(status);
		problemDetail.setExceptionClassName(exception.getClass().getSimpleName());
		
		if (exception instanceof BlueskyException blueskyException) {
			var errorCodes = getBlueskyExceptionErrorCodes(blueskyException);
			log.debug("[BlueskyException error message] code : {}", Arrays.deepToString(errorCodes));
			var defaultMessageSourceResolvable = new DefaultMessageSourceResolvable(errorCodes, blueskyException.getErrorMessageArgs(), blueskyException.getMessage() == null ? blueskyException.getErrorCode() : blueskyException.getMessage());
	    	var localizedMessage = messageSourceAccessor.getMessage(defaultMessageSourceResolvable);
			
			problemDetail.setErrorCode(blueskyException.getErrorCode());
			problemDetail.setErrorMessageArgs(blueskyException.getErrorMessageArgs());
			if (!StringUtils.hasText(localizedMessage) || localizedMessage.equals(blueskyException.getErrorCode())) {
				problemDetail.setMessage(blueskyException.getMessage());
			} else {
				problemDetail.setMessage(localizedMessage);
				problemDetail.setDisplayableMessage(true);
			}
			problemDetail.setObject(blueskyException.getErrorCode());
			problemDetail.setExceptionClassName(blueskyException.getClass().getSimpleName());
		} else {
			
			var errorCodes = messageCodesResolver.resolveMessageCodes(exception.getClass().getSimpleName(), null);
			log.debug("[Exception error message] code : {}", Arrays.asList(errorCodes));
			var defaultMessageSourceResolvable = new DefaultMessageSourceResolvable(errorCodes,  exception.getLocalizedMessage());
			var localizedMessage = getMessage(defaultMessageSourceResolvable);
			problemDetail.setMessage(localizedMessage);
			if (!localizedMessage.equals(exception.getLocalizedMessage())) {
				problemDetail.setDisplayableMessage(true);
			}
			
		}
		return problemDetail;
	}
	
//	/**
//	 * api 호출로 전달받은 ErrorMessage를 재가공하는 경우 
//	 * @param blueskyException
//	 * @return
//	 */
//	public static BlueskyProblemDetail getErrorMessageFromErrorMessage(BlueskyException blueskyException) {
//		if (blueskyException.getErrorMessage() == null) {
//			return null;
//		}
//		if (!(blueskyException.getErrorMessage() instanceof BlueskyProblemDetail)) {
//			// TODO 기타 유형의 errorMessage에 대한 처리는 어떻게 하면 좋을까?
//			return null;
//		}
//		return getErrorMessage(blueskyException.getClass().getSimpleName(), (BlueskyProblemDetail) blueskyException.getErrorMessage());
//	}
//	
//	/**
//	 * api 호출로 전달받은 ErrorMessageList를 재가공하는 경우 
//	 * @param blueskyException
//	 * @return
//	 */
//	public static List<BlueskyProblemDetail> getErrorMessageListFromErrorMessageList(BlueskyException blueskyException) {
//		if (blueskyException.getErrorMessageList() == null) {
//			return Collections.emptyList();
//		}
//		if (blueskyException.getErrorMessageList().get(0) instanceof BlueskyProblemDetail) {
//			// TODO 기타 유형의 errorMessage에 대한 처리는 어떻게 하면 좋을까?
//			return Collections.emptyList();
//		}
//		
//		var errorMessageList = new ArrayList<BlueskyProblemDetail>();
//		for (ErrorMessage errorMessage : blueskyException.getErrorMessageList()) {
//			errorMessageList.add(getErrorMessage(blueskyException.getClass().getSimpleName(), (BlueskyProblemDetail) errorMessage));
//		}
//		return errorMessageList;
//	}
//	
//	private static BlueskyProblemDetail getErrorMessage(String exceptionName, BlueskyProblemDetail errorMessage) {
//		// 로컬메세지 처리
//		if (errorMessage.getErrorCode() != null) {
//			var errorCode = exceptionName + "." + errorMessage.getErrorCode();
//			log.debug("[BlueskyException error message from errorMessageInterface] code : {}", exceptionName + "." + errorMessage.getErrorCode());
//			errorMessage.setMessage(messageSourceAccessor.getMessage(errorCode, errorMessage.getErrorMessageArgs(), errorMessage.getMessage() == null ? errorMessage.getErrorCode() : errorMessage.getMessage()));
//		}
//		return errorMessage;
//	}
//	
//	public static List<BlueskyProblemDetail> getErrorMessageList(int status, Exception exception) {
//		if (!(exception instanceof BindException) && !(exception instanceof MethodArgumentNotValidException)) {
//			return Collections.emptyList();
//		}
//		BindingResult bindingResult = null;
//		if (exception instanceof BindException bindException) {
//			bindingResult = bindException.getBindingResult();
//		}
//		
//		if (bindingResult == null) {
//			return Collections.emptyList();
//		}
//		
//		var errorMessageList = new ArrayList<BlueskyProblemDetail>();
//		var objectErrorList = bindingResult.getFieldErrors().isEmpty() ? bindingResult.getAllErrors() : bindingResult.getFieldErrors();
//		for (var objectError : objectErrorList) {
//			BlueskyProblemDetail errorMessage = new BlueskyProblemDetail();
//			errorMessage.setExceptionClassName(exception.getClass().getSimpleName());
//			errorMessage.setMessage(messageSourceAccessor.getMessage(objectError));
//			errorMessage.setObject(objectError.getObjectName());
//			errorMessage.setDisplayableMessage(true);
//			errorMessage.setErrorCode(objectError.getCode());
//			if (objectError instanceof FieldError fieldError) {
//				errorMessage.setField(fieldError.getField());
//			}
//			errorMessageList.add(errorMessage);
//			log.debug("[MethodArgumentNotValidException error message] code : {}, arguments : {}", Arrays.asList(objectError.getCodes()), Arrays.asList(objectError.getArguments()));
//		}
//		return errorMessageList;
//	}
	
	/**
	 * BlueskyException을 extends 한 Exception과 BlueskyException 간 errorCodes 를 반환하기 위해 구현
	 * 예를 들어 AnotherException.preorder.preOrderRecord.NOT_EXIST_SCHEDULE 가 발생한 경우 결과는 아래와 같다.
	 * <ul>
	 * <li>AnotherException.preorder.preOrderRecord.NOT_EXIST_SCHEDULE</li>
	 * <li>BlueskyException.preorder.preOrderRecord.NOT_EXIST_SCHEDULE</li>
	 * <li>PreOrderException</li>
	 * <li>BlueskyException</li>
	 * </ul>
	 * @param exception
	 * @return
	 */
	private static String[] getBlueskyExceptionErrorCodes(BlueskyException exception) {
		var errorCodes = messageCodesResolver.resolveMessageCodes(exception.getClass().getSimpleName(), exception.getErrorCode());
		
		if (exception.getClass().isAssignableFrom(BlueskyException.class)) {
			return errorCodes;
		}
		var blueskyErrorCodes = messageCodesResolver.resolveMessageCodes(BlueskyException.class.getSimpleName(), exception.getErrorCode());
		var errorCodeList = new ArrayList<String>();
		errorCodeList.addAll(Arrays.asList(errorCodes));
		errorCodeList.addAll(Arrays.asList(blueskyErrorCodes));
		errorCodeList.sort((v1, v2) -> v1.chars().filter(e -> e == '.').count() > v2.chars().filter(e -> e == '.').count() ? -1 : 0 );
		return errorCodeList.toArray(new String[errorCodeList.size()]);
	}
}
