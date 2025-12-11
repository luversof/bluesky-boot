package io.github.luversof.boot.exception;

import java.util.Arrays;
import java.util.Objects;

/**
 * An error message object for use in error response handling.
 */
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
	private String[] errorMessageArgs; // arg가 String으로 현재 강제인데 Object로 변경이 가능할까?
	private String exceptionClassName;
	private boolean isDisplayableMessage = false; // 에러 메세지 화면 표시 가능여부
	private String message;
	private String object; // bindException의 경우 에러 발생 ObjectName을 전달, 보통의 경우 code를 전달
	private String field; // bindException의 경우 에러 발생 field를 전달

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String[] getErrorMessageArgs() {
		return errorMessageArgs;
	}

	public void setErrorMessageArgs(String[] errorMessageArgs) {
		this.errorMessageArgs = errorMessageArgs;
	}

	public String getExceptionClassName() {
		return exceptionClassName;
	}

	public void setExceptionClassName(String exceptionClassName) {
		this.exceptionClassName = exceptionClassName;
	}

	public boolean isDisplayableMessage() {
		return isDisplayableMessage;
	}

	public void setDisplayableMessage(boolean displayableMessage) {
		isDisplayableMessage = displayableMessage;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		BlueskyErrorMessage that = (BlueskyErrorMessage) o;
		return isDisplayableMessage == that.isDisplayableMessage && Objects.equals(errorCode, that.errorCode)
				&& Arrays.equals(errorMessageArgs, that.errorMessageArgs)
				&& Objects.equals(exceptionClassName, that.exceptionClassName) && Objects.equals(message, that.message)
				&& Objects.equals(object, that.object) && Objects.equals(field, that.field);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(errorCode, exceptionClassName, isDisplayableMessage, message, object, field);
		result = 31 * result + Arrays.hashCode(errorMessageArgs);
		return result;
	}

	@Override
	public String toString() {
		return "BlueskyErrorMessage{" +
				"errorCode='" + errorCode + '\'' +
				", errorMessageArgs=" + Arrays.toString(errorMessageArgs) +
				", exceptionClassName='" + exceptionClassName + '\'' +
				", isDisplayableMessage=" + isDisplayableMessage +
				", message='" + message + '\'' +
				", object='" + object + '\'' +
				", field='" + field + '\'' +
				'}';
	}
}
