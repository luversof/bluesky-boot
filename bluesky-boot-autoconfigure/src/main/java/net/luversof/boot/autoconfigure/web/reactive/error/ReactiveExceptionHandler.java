package net.luversof.boot.autoconfigure.web.reactive.error;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ConditionalOnWebApplication(type = Type.REACTIVE)
@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class ReactiveExceptionHandler {

}
