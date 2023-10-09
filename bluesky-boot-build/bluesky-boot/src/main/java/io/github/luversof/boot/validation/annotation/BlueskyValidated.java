package io.github.luversof.boot.validation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.validation.annotation.Validated;

/**
 * Annotation provided to facilitate the use of @Validated in non-controller layers
 * @author bluesky
 *
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Validated
public @interface BlueskyValidated {

	/**
	 * validation hint class
	 * @return
	 */
	Class<?>[] value() default {};

}
