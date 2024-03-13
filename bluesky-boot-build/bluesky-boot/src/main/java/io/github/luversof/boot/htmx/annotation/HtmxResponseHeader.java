package io.github.luversof.boot.htmx.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.github.luversof.boot.htmx.constant.HtmxResponseHeaderName;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Repeatable(HtmxResponseHeaders.class)
public @interface HtmxResponseHeader {
	
	/**
	 * header에 추가할 값
	 * 이거 pathVariable 적용 가능한지 확인이 필요함
	 * @return
	 */
	String value() default "";

	/**
	 * header의 name
	 * @return
	 */
	HtmxResponseHeaderName headerName() default HtmxResponseHeaderName.HX_TRIGGER_AFTER_SETTLE;
	
	
}
