package io.github.luversof.boot.exception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.DefaultMessageCodesResolver;
import org.springframework.validation.FieldError;
import org.springframework.validation.MessageCodesResolver;

import io.github.luversof.boot.context.BlueskyContextHolder;
import io.github.luversof.boot.core.CoreProperties;
import io.github.luversof.boot.util.ApplicationContextUtil;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class ProblemDetailUtil {
	
	private static final String MESSAGE_CODES = "[BlueskyException error message] code : {}";
	
	private static final String EXCEPTION_PARAMETER = "result";

	private static MessageCodesResolver messageCodesResolver = new DefaultMessageCodesResolver();
	
	private static MessageSourceAccessor messageSourceAccessor;
	
	private static MessageSourceAccessor getMessageSourceAccessor() {
		if (messageSourceAccessor == null) {
			messageSourceAccessor = ApplicationContextUtil.getApplicationContext().getBean(MessageSourceAccessor.class);
		}
		return messageSourceAccessor;
	}
	
	public static <T extends BlueskyException> ProblemDetail getProblemDetail(T exception) {
		var problemDetail = ProblemDetail.forStatus(exception.getStatus());

		// api 호출로 발생한 BrickException의 경우 exception의 errorMessage또는 errorMessageList에 값이 있음
		// api 호출로 발생한 BrickException의 errorMessage의 경우 확장된 errorMessage를 사용
		if (exception.getErrorMessage() != null) {
			problemDetail.setProperty(EXCEPTION_PARAMETER, convertBrickErrorMessage(exception.getErrorMessage()));
			return problemDetail;
		}
		
		// api 호출로 발생한 BrickException의 errorMessageList는 bindException 결과로 BrickErrorMessageList 고정임
		if (exception.getErrorMessageList() != null) {
			List<BlueskyErrorMessage> errorMessageList = new ArrayList<>();
			exception.getErrorMessageList().stream().forEach(x -> {
				BlueskyErrorMessage targetBlueskyErrorMessage = (BlueskyErrorMessage) x;  
				setBlueskyErrorMessage(targetBlueskyErrorMessage);
				errorMessageList.add(targetBlueskyErrorMessage);
			});
			problemDetail.setProperty(EXCEPTION_PARAMETER, errorMessageList);
			return problemDetail;
		}
		
		problemDetail.setProperty(EXCEPTION_PARAMETER, getBrickErrorMessage(exception));
		return problemDetail;
	}
	
	/**
	 * BrickException에서 BrickErrorMessage 객체를 반환
	 * @param exception
	 * @return
	 */
	private static <T extends BlueskyException> BlueskyErrorMessage getBrickErrorMessage(T exception) {
		
		var messageCodes = getExceptionErrorCodes(exception);
		log.debug(MESSAGE_CODES, List.of(messageCodes));
		log.error("BrickException occurred", exception);
		var defaultMessageSourceResolvable = new DefaultMessageSourceResolvable(messageCodes, exception.getErrorMessageArgs(), exception.getMessage() == null ? exception.getErrorCode() : exception.getMessage());
    	var localizedMessage = getMessageSourceAccessor().getMessage(defaultMessageSourceResolvable);
		
		var errorMessage = new BlueskyErrorMessage();
		errorMessage.setErrorCode(exception.getErrorCode());
		errorMessage.setErrorMessageArgs(exception.getErrorMessageArgs());
		if (StringUtils.hasLength(localizedMessage) && !localizedMessage.equals(exception.getErrorCode()) && !localizedMessage.equals(getMessageSourceAccessor().getMessage("BrickException"))) {
			errorMessage.setMessage(localizedMessage);
			errorMessage.setDisplayableMessage(true);
		} else {
			errorMessage.setMessage(exception.getMessage());
		}		

		errorMessage.setObject(exception.getErrorCode());
		errorMessage.setExceptionClassName(exception.getClass().getSimpleName());
		
		return errorMessage;
	}
	
	private static <T extends BlueskyException> String[] getExceptionErrorCodes(T exception) {
		var blueskyContext= BlueskyContextHolder.getContext();
		String moduleName = blueskyContext == null ? null : blueskyContext.getModuleName();
		
		var errorCodes = messageCodesResolver.resolveMessageCodes(
			exception.getClass().getSimpleName(),
			moduleName,
			exception.getErrorCode() == null ? "" : exception.getErrorCode(),
			null
		);
		
		if (exception.getClass().isAssignableFrom(BlueskyException.class)) {
			return errorCodes;
		}
		
		// BlueskyException을 상속받은 하위 Exception을 사용하면 BlueskyException에 대한 errorCode도 계산하여 가장 긴 순서대로 반환함
		var blueskyErrorCodes = messageCodesResolver.resolveMessageCodes(
			BlueskyException.class.getSimpleName(),
			moduleName,
			exception.getErrorCode() == null ? "" : exception.getErrorCode(),
			null
		);
		var messageCodeList = new ArrayList<String>();
		messageCodeList.addAll(Arrays.asList(errorCodes));
		messageCodeList.addAll(Arrays.asList(blueskyErrorCodes));
		messageCodeList.sort((v1, v2) -> v1.chars().filter(e -> e == '.').count() > v2.chars().filter(e -> e == '.').count() ? -1 : 0 );
		return messageCodeList.toArray(new String[messageCodeList.size()]);
	}
	
	/**
	 * 획득한 ErrorMessage 객체를 BrickErrorMessage로 변경
	 * @param errorMessage
	 * @return
	 */
	private static <T extends ErrorMessage> BlueskyErrorMessage convertBrickErrorMessage(T errorMessage) {
		BlueskyErrorMessage targetErrorMessage = null;
		
		if (errorMessage instanceof BlueskyErrorMessage brickErrorMessage) {
			targetErrorMessage = brickErrorMessage;
		}

		setBlueskyErrorMessage(targetErrorMessage);
		return targetErrorMessage;
	}
	
	/**
	 * api 호출을 통해 획득한 BrickErrorMessage의 경우 해당 프로젝트에서 message를 override 할 수 있음 
	 * @param blueskyErrorMessage
	 */
	private static void setBlueskyErrorMessage(BlueskyErrorMessage blueskyErrorMessage) {
		if (blueskyErrorMessage.getErrorCode() == null) {
			return;
		}
		
		var messageCodes = messageCodesResolver.resolveMessageCodes(
			blueskyErrorMessage.getErrorCode(),				
			blueskyErrorMessage.getObject(), 
			blueskyErrorMessage.getField() == null ? "" : blueskyErrorMessage.getField(),
			null
		);
		log.debug(MESSAGE_CODES, List.of(messageCodes));
		
		var defaultMessageSourceResolvable = new DefaultMessageSourceResolvable(
			messageCodes,
			blueskyErrorMessage.getErrorMessageArgs(),
			blueskyErrorMessage.getMessage() == null ? blueskyErrorMessage.getErrorCode() : blueskyErrorMessage.getMessage()
		);
		var localizedMessage = getMessageSourceAccessor().getMessage(defaultMessageSourceResolvable);
		
		if (StringUtils.hasLength(localizedMessage) && !localizedMessage.equals(blueskyErrorMessage.getMessage())) {
			blueskyErrorMessage.setMessage(localizedMessage);
			blueskyErrorMessage.setDisplayableMessage(true);
		}
	}

	public static <T extends BindException> ProblemDetail getProblemDetail(T exception) {
		var problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
		problemDetail.setProperty(EXCEPTION_PARAMETER, getBlueskyErrorMessageList(exception));
		return problemDetail;
	}
	
	private static <T extends BindException> List<BlueskyErrorMessage> getBlueskyErrorMessageList(T exception) {
		var brickErrorMessageList = new ArrayList<BlueskyErrorMessage>();
		var bindingResult = exception.getBindingResult();
		var objectErrorList = bindingResult.getFieldErrors().isEmpty() ? bindingResult.getAllErrors() : bindingResult.getFieldErrors();
		
		objectErrorList.forEach(objectError -> {
			var blueskyErrorMessage = new BlueskyErrorMessage();
			blueskyErrorMessage.setExceptionClassName(exception.getClass().getSimpleName());
			blueskyErrorMessage.setMessage(getMessageSourceAccessor().getMessage(objectError));
			blueskyErrorMessage.setObject(objectError.getObjectName());
			blueskyErrorMessage.setDisplayableMessage(true);
			blueskyErrorMessage.setErrorCode(objectError.getCode());
			if (objectError instanceof FieldError fieldError) {
				blueskyErrorMessage.setField(fieldError.getField());
			}
			brickErrorMessageList.add(blueskyErrorMessage);
			log.debug("[MethodArgumentNotValidException error message] code : {}, arguments : {}", Arrays.asList(objectError.getCodes()), Arrays.asList(objectError.getArguments()));
		});
		
		return brickErrorMessageList;
	}

	public static <T extends Throwable> ProblemDetail getProblemDetail(T exception) {
		return getProblemDetail(exception, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	public static <T extends Throwable> ProblemDetail getProblemDetail(T exception, HttpStatusCode status) {
		// 로그 처리를 예외 처리한 경우는 제외
		var coreProperties = ApplicationContextUtil.getApplicationContext().getBean(CoreProperties.class);
		if (!coreProperties.getLogExceptExceptionList().contains(exception.getClass().getSimpleName())) {
			log.error("Throwable exception : ", exception);
		}
		
		var brickErrorMessage = new BlueskyErrorMessage();
		brickErrorMessage.setMessage(exception.getMessage());
		brickErrorMessage.setExceptionClassName(exception.getClass().getSimpleName());
		
		var localizedMessage = getMessageSourceAccessor().getMessage(exception.getClass().getSimpleName(), exception.getMessage());
		if (!localizedMessage.equals(brickErrorMessage.getMessage())) {
			brickErrorMessage.setMessage(localizedMessage);
			brickErrorMessage.setDisplayableMessage(true);
		}
		
		var problemDetail = ProblemDetail.forStatus(status);
		problemDetail.setProperty(EXCEPTION_PARAMETER, brickErrorMessage);
		return problemDetail;
	}
	
}
