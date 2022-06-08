package io.github.luversof.boot.autoconfigure.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.validation.annotation.Validated;

/**
 * controller가 아닌 layer에서 @Validated 를 쉽게 사용하기 위해 제공되는 annotation
 * @author bluesky
 *
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Validated
public @interface BlueskyValidated {

	Class<?>[] value() default {};

}
