package io.github.luversof.boot.exception;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class BlueskyException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	private static final int DEFAULT_STATUS = 400;

	private final String errorCode;
	private String[] errorMessageArgs;
	
	private String localizedMessage;
	
	/**
	 * exception의 처리 httpStatus
	 * httpStatus 설정에 따른 에러 처리를 함
	 * default 400
	 */
	private final int status;
	
	/**
	 * api 호출 시엔 errorMessage를 그대로 담아서 반환함
	 * 단일 반환 케이스와 복수 반환 케이스에 대응하기 위해 아래와 같이 처리하였음.
	 * 리스트에 단일 반환도 추가하는 식으로 변경 고려 필요
	 */
	private ErrorMessage errorMessage;
	private List<ErrorMessage> errorMessageList;
	
	public BlueskyException(String errorCode, int status) {
		this.errorCode = errorCode;
		this.status = status;
	}
	
	public BlueskyException(String errorCode) {
		this(errorCode, DEFAULT_STATUS);
	}
	
	
	public BlueskyException(Enum<?> errorCode) {
		this(errorCode.getClass().getSimpleName() + "." + errorCode.name(), DEFAULT_STATUS);
	}
	
	public BlueskyException(Enum<?> errorCode, int status) {
		this(errorCode.getClass().getSimpleName() + "." + errorCode.name(), status);
	}
	
	public BlueskyException(ErrorMessage errorMessage) {
		this("API_EXCEPTION", DEFAULT_STATUS);
		this.errorMessage = errorMessage;
	}
	
	public BlueskyException(List<ErrorMessage> errorMessageList) {
		this("API_EXCEPTION", DEFAULT_STATUS);
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
