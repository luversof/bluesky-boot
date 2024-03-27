package io.github.luversof.boot.exception;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BlueskyException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private  String errorCode;
	private String[] errorMessageArgs;
	
	private String localizedMessage;
	
	/**
	 * exception의 처리 httpStatus
	 * httpStatus 설정에 따른 에러 처리를 함
	 * default 400
	 */
	private int status = 400;
	
	/**
	 * api 호출 시엔 errorMessage를 그대로 담아서 반환함
	 */
	private ErrorMessage errorMessage;
	private List<ErrorMessage> errorMessageList;
	
	public BlueskyException(String errorCode) {
		this.errorCode = errorCode;
	}
	
	public BlueskyException(String errorCode, int status) {
		this.errorCode = errorCode;
		this.status = status;
	}
	
	public BlueskyException(Enum<?> errorCode) {
		this.errorCode = errorCode.getClass().getSimpleName() + "." + errorCode.name();
	}
	
	public BlueskyException(Enum<?> errorCode, int status) {
		this.errorCode = errorCode.getClass().getSimpleName() + "." + errorCode.name();
		this.status = status;
	}
	
	public BlueskyException(ErrorMessage errorMessage) {
		this.errorCode = "API_EXCEPTION";
		this.errorMessage = errorMessage;
	}
	
	public BlueskyException(List<ErrorMessage> errorMessageList) {
		this.errorCode = "API_EXCEPTION";
		this.errorMessageList = errorMessageList;
	}
	
	public BlueskyException setErrorMessageArgs(String... errorMessageArgs) {
		this.errorMessageArgs = errorMessageArgs;
		return this;
	}
	
	@Override
	public String getLocalizedMessage() {
		return this.localizedMessage;
	}
	
}
