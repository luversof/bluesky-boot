package io.github.luversof.boot.exception;

import lombok.Data;

/**
 * An error message object for use in error response handling.
 */
@Data
public class BlueskyErrorMessage implements ErrorMessage {

	private static final long serialVersionUID = 1L;

	/**
	 * Set errorMessage if it requires arguments
	 * 
	 * @param errorMessageArgs errorMessage arguments
	 * @return BlueskyErrorMessage
	 */
	public BlueskyErrorMessage setErrorMessageArgs(String... errorMessageArgs) {
		this.errorMessageArgs = errorMessageArgs;
		return this;
	}

	private String errorCode;
	private String[] errorMessageArgs;
	private String exceptionClassName;
	private boolean isDisplayableMessage = false;	//에러 메세지 화면 표시 가능여부
	private String message;
	private String object;		//bindException의 경우 에러 발생 ObjectName을 전달, 보통의 경우 code를 전달
	private String field;		//bindException의 경우 에러 발생 field를 전달

}
