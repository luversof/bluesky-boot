package net.luversof.boot.autoconfigure.context;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.luversof.boot.exception.BlueskyErrorMessage;
import net.luversof.boot.exception.BlueskyException;
import net.luversof.boot.exception.ErrorMessage;

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
	
	public static BlueskyErrorMessage getErrorMessage(Throwable exception) {
		var errorMessage = new BlueskyErrorMessage();
		errorMessage.setExceptionClassName(exception.getClass().getSimpleName());
		
		if (exception instanceof BlueskyException) {
			var blueskyException = (BlueskyException) exception;

			var errorCodes = getBlueskyExceptionErrorCodes(blueskyException);
			log.debug("[BlueskyException error message] code : {}", Arrays.deepToString(errorCodes));
			var defaultMessageSourceResolvable = new DefaultMessageSourceResolvable(errorCodes, blueskyException.getErrorMessageArgs(), blueskyException.getMessage() == null ? blueskyException.getErrorCode() : blueskyException.getMessage());
	    	var localizedMessage = messageSourceAccessor.getMessage(defaultMessageSourceResolvable);
			
			errorMessage.setErrorCode(blueskyException.getErrorCode());
			errorMessage.setErrorMessageArgs(blueskyException.getErrorMessageArgs());
			if (StringUtils.isEmpty(localizedMessage) || localizedMessage.equals(blueskyException.getErrorCode())) {
				errorMessage.setMessage(blueskyException.getMessage());
			} else {
				errorMessage.setMessage(localizedMessage);
				errorMessage.setDisplayableMessage(true);
			}
			errorMessage.setObject(blueskyException.getErrorCode());
			errorMessage.setExceptionClassName(blueskyException.getClass().getSimpleName());
		} else {
			
			var errorCodes = messageCodesResolver.resolveMessageCodes(exception.getClass().getSimpleName(), null);
			log.debug("[Exception error message] code : {}", Arrays.asList(errorCodes));
			var defaultMessageSourceResolvable = new DefaultMessageSourceResolvable(errorCodes,  exception.getLocalizedMessage());
			var localizedMessage = getMessage(defaultMessageSourceResolvable);
			errorMessage.setMessage(localizedMessage);
			if (localizedMessage != null && !localizedMessage.equals(exception.getLocalizedMessage())) {
				errorMessage.setDisplayableMessage(true);
			}
			
		}
		return errorMessage;
	}
	
	/**
	 * api 호출로 전달받은 ErrorMessage를 재가공하는 경우 
	 * @param blueskyException
	 * @return
	 */
	public static BlueskyErrorMessage getErrorMessageFromErrorMessage(BlueskyException blueskyException) {
		if (blueskyException.getErrorMessage() == null) {
			return null;
		}
		if (!(blueskyException.getErrorMessage() instanceof BlueskyErrorMessage)) {
			// TODO 기타 유형의 errorMessage에 대한 처리는 어떻게 하면 좋을까?
			return null;
		}
		return getErrorMessage(blueskyException.getClass().getSimpleName(), (BlueskyErrorMessage) blueskyException.getErrorMessage());
	}
	
	/**
	 * api 호출로 전달받은 ErrorMessageList를 재가공하는 경우 
	 * @param blueskyException
	 * @return
	 */
	public static List<BlueskyErrorMessage> getErrorMessageListFromErrorMessageList(BlueskyException blueskyException) {
		if (blueskyException.getErrorMessageList() == null) {
			return Collections.emptyList();
		}
		if (blueskyException.getErrorMessageList().get(0) instanceof BlueskyErrorMessage) {
			// TODO 기타 유형의 errorMessage에 대한 처리는 어떻게 하면 좋을까?
			return Collections.emptyList();
		}
		
		var errorMessageList = new ArrayList<BlueskyErrorMessage>();
		for (ErrorMessage errorMessage : blueskyException.getErrorMessageList()) {
			errorMessageList.add(getErrorMessage(blueskyException.getClass().getSimpleName(), (BlueskyErrorMessage) errorMessage));
		}
		return errorMessageList;
	}
	
	private static BlueskyErrorMessage getErrorMessage(String exceptionName, BlueskyErrorMessage errorMessage) {
		var targetErrorMessage = (BlueskyErrorMessage) errorMessage;
		// 로컬메세지 처리
		if (targetErrorMessage.getErrorCode() != null) {
			var errorCode = exceptionName + "." + targetErrorMessage.getErrorCode();
			log.debug("[BlueskyException error message from errorMessageInterface] code : {}", exceptionName + "." + targetErrorMessage.getErrorCode());
			targetErrorMessage.setMessage(messageSourceAccessor.getMessage(errorCode, targetErrorMessage.getErrorMessageArgs(), targetErrorMessage.getMessage() == null ? targetErrorMessage.getErrorCode() : targetErrorMessage.getMessage()));
		}
		return targetErrorMessage;
	}
	
	public static List<BlueskyErrorMessage> getErrorMessageList(Exception exception) {
		if (!(exception instanceof BindException) && !(exception instanceof MethodArgumentNotValidException)) {
			return Collections.emptyList();
		}
		BindingResult bindingResult = null;
		if (exception instanceof BindException) {
			bindingResult = ((BindException) exception).getBindingResult();
		} else if (exception instanceof MethodArgumentNotValidException) {
			bindingResult = ((MethodArgumentNotValidException) exception).getBindingResult();
		}
		
		if (bindingResult == null) {
			return Collections.emptyList();
		}
		
		var errorMessageList = new ArrayList<BlueskyErrorMessage>();
		var objectErrorList = bindingResult.getFieldErrors().isEmpty() ? bindingResult.getAllErrors() : bindingResult.getFieldErrors();
		for (var objectError : objectErrorList) {
			BlueskyErrorMessage errorMessage = new BlueskyErrorMessage();
			errorMessage.setExceptionClassName(exception.getClass().getSimpleName());
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
