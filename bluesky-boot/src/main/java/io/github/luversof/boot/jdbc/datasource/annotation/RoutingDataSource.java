package io.github.luversof.boot.jdbc.datasource.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RoutingDataSource {
	
	/**
	 * lookupKey를 직접 지정하여 사용할 경우 설정
	 * @return
	 */
	String value() default "";
	
	/**
	 * resolver를 사용할 경우 등록된 resolver beanName을 설정
	 * @return
	 */
	String resolver() default "";
}
