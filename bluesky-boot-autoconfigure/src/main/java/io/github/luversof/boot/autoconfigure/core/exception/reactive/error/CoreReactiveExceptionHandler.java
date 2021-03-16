package io.github.luversof.boot.autoconfigure.core.exception.reactive.error;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class CoreReactiveExceptionHandler {

}
