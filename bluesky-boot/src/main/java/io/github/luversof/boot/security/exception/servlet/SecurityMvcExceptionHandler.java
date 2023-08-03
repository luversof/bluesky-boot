package io.github.luversof.boot.security.exception.servlet;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedCredentialsNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import io.github.luversof.boot.context.MessageUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE + 10)
public class SecurityMvcExceptionHandler {
	
	@ExceptionHandler
	@ResponseStatus(value = HttpStatus.UNAUTHORIZED)
	public ModelAndView preAuthenticatedCredentialsNotFoundException(PreAuthenticatedCredentialsNotFoundException exception) {
		log.error("PreAuthenticatedCredentialsNotFoundException exception", exception);
		return new ModelAndView("login");
	}

	@ExceptionHandler
	public ProblemDetail accessDeniedException(AccessDeniedException exception) {
		log.error("AccessDeniedException exception", exception);
		return MessageUtil.getProblemDetail(HttpStatus.UNAUTHORIZED, exception);
	}
}
