package io.github.luversof.boot.exception;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.util.Assert;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Exception 정보를 전달하기 위해 쓰이는 객체
 * 에러 발생시 이 객체로 반환함
 * @author bluesky
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BlueskyProblemDetail extends ProblemDetail {
	
	protected BlueskyProblemDetail(int rawStatusCode) {
		this.setStatus(rawStatusCode);
	}
	
	public BlueskyProblemDetail setErrorMessageArgs(String... errorMessageArgs) {
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
	
	
	public static BlueskyProblemDetail forStatus(HttpStatusCode status) {
		Assert.notNull(status, "HttpStatusCode is required");
		return forStatus(status.value());
	}

	public static BlueskyProblemDetail forStatus(int status) {
		return new BlueskyProblemDetail(status);
	}
	
	public static BlueskyProblemDetail forStatusAndDetail(HttpStatusCode status, String detail) {
		Assert.notNull(status, "HttpStatusCode is required");
		BlueskyProblemDetail problemDetail = forStatus(status.value());
		problemDetail.setDetail(detail);
		return problemDetail;
	}
}
