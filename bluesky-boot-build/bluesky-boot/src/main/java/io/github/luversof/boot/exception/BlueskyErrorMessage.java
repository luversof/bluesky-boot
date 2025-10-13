package io.github.luversof.boot.exception;

import java.util.Arrays;
import java.util.Objects;

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
	public BlueskyErrorMessage setErrorMessageArgs(Object[] errorMessageArgs) {
		if (errorMessageArgs != null && errorMessageArgs.length > 0) {
			this.errorMessageArgs = Arrays.stream(errorMessageArgs)
				.filter(Objects::nonNull) // null 값 제거
				.map(Object::toString)
				.toArray(String[]::new);
		}
		
		return this;
	}

	private String errorCode;
	private String[] errorMessageArgs;	// arg가 String으로 현재 강제인데 Object로 변경이 가능할까?
	private String exceptionClassName;
	private boolean isDisplayableMessage = false;	//에러 메세지 화면 표시 가능여부
	private String message;
	private String object;		//bindException의 경우 에러 발생 ObjectName을 전달, 보통의 경우 code를 전달
	private String field;		//bindException의 경우 에러 발생 field를 전달

}
