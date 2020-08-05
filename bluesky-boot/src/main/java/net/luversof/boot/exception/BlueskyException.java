package net.luversof.boot.exception;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BlueskyException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private final String errorCode;
	private Enum<?> errorCodeEnum;

	private String localizedMessage;
	
	private String errorPage = BlueskyErrorPage.SIMPLE.getViewName();
	
	private String[] errorMessageArgs;
	
	public BlueskyException setErrorMessageArgs(String... errorMessageArgs) {
		this.errorMessageArgs = errorMessageArgs;
		return this;
	}
	
	/**
	 * api 호출 시엔 errorMessage를 그대로 담아서 반환함
	 */
	private ErrorMessage errorMessage;
	private List<ErrorMessage> errorMessageList;

	public BlueskyException(String errorCode) {
		this.errorCode = errorCode;
	}
	
	public BlueskyException(String errorCode, ErrorPage errorPage) {
		this.errorCode = errorCode;
		this.errorPage = errorPage.getViewName();
	}
	
	public BlueskyException(String errorCode, String errorPage) {
		this.errorCode = errorCode;
		this.errorPage = errorPage;
	}
	
	public BlueskyException(Enum<?> errorCode) {
		this.errorCodeEnum = errorCode;
		this.errorCode = errorCode.getClass().getSimpleName() + "." + errorCode.name();
	}
	
	public BlueskyException(Enum<?> errorCode, ErrorPage errorPage) {
		this.errorCodeEnum = errorCode;
		this.errorCode = errorCode.getClass().getSimpleName() + "." + errorCode.name();
		this.errorPage = errorPage.getViewName();
	}
	
	public BlueskyException(Enum<?> errorCode, String errorPage) {
		this.errorCodeEnum = errorCode;
		this.errorCode = errorCode.getClass().getSimpleName() + "." + errorCode.name();
		this.errorPage = errorPage;
	}
	
	public BlueskyException(ErrorMessage errorMessage) {
		this.errorCode = "API_EXCEPTION";
		this.errorMessage = errorMessage;
	}
	
	public BlueskyException(List<ErrorMessage> errorMessageList) {
		this.errorCode = "API_EXCEPTION";
		this.errorMessageList = errorMessageList;
	}
	
	@Override
	public String getLocalizedMessage() {
		return this.localizedMessage;
	}
}
