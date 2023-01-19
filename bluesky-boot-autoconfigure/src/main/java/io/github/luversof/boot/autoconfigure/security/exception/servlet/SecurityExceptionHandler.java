package io.github.luversof.boot.autoconfigure.security.exception.servlet;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import io.github.luversof.boot.autoconfigure.context.MessageUtil;
import io.github.luversof.boot.exception.BlueskyExceptionHandler;
import io.github.luversof.boot.exception.BlueskyProblemDetail;

@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE + 10)
public class SecurityExceptionHandler extends BlueskyExceptionHandler {
	
	@ExceptionHandler
	@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
	public ModelAndView preAuthenticatedCredentialsNotFoundException(PreAuthenticatedCredentialsNotFoundException exception) {
		return new ModelAndView("login");
	}

	@ExceptionHandler
	public BlueskyProblemDetail accessDeniedException(AccessDeniedException exception) {
		return MessageUtil.getProblemDetail(HttpStatus.UNAUTHORIZED, exception);
	}
}
