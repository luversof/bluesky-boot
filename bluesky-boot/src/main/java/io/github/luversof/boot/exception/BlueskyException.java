package io.github.luversof.boot.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BlueskyException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private final String errorCode;
	private String[] errorMessageArgs;
	
	public BlueskyException setErrorMessageArgs(String... errorMessageArgs) {
		this.errorMessageArgs = errorMessageArgs;
		return this;
	}

	public BlueskyException(String errorCode) {
		this.errorCode = errorCode;
	}
	
	public BlueskyException(Enum<?> errorCode) {
		this.errorCode = errorCode.getClass().getSimpleName() + "." + errorCode.name();
	}
	
}
