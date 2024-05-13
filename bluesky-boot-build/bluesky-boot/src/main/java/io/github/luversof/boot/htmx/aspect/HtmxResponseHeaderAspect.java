package io.github.luversof.boot.htmx.aspect;

import java.util.HashMap;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import io.github.luversof.boot.expression.SpelParserUtil;
import io.github.luversof.boot.htmx.annotation.HtmxResponseHeader;

@Aspect
public class HtmxResponseHeaderAspect {

	@Around("@within(htmxResponseHeader)")
	public Object classAround(ProceedingJoinPoint proceedingJoinPoint, HtmxResponseHeader htmxResponseHeader) throws Throwable {
		return execute(proceedingJoinPoint, htmxResponseHeader);
	}
	
	@Around("@annotation(htmxResponseHeader)")
	public Object methodAround(ProceedingJoinPoint proceedingJoinPoint, HtmxResponseHeader htmxResponseHeader) throws Throwable {
		return execute(proceedingJoinPoint, htmxResponseHeader);
	}
	
	private Object execute(ProceedingJoinPoint proceedingJoinPoint, HtmxResponseHeader htmxResponseHeader) throws Throwable {
		
		String[] parameterNames = ((MethodSignature) proceedingJoinPoint.getSignature()).getParameterNames();
		Object[] args = proceedingJoinPoint.getArgs();
		var map = new HashMap<>();
		for (int i = 0; i < parameterNames.length; i++) {
			map.put(parameterNames[i], args[i]);
		}
		String parseStr = SpelParserUtil.parse(htmxResponseHeader.value(), map);
		
		var response = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getResponse();
		if (response != null) {
			response.setHeader(htmxResponseHeader.headerName().getHeaderName(), parseStr);
		}
		return proceedingJoinPoint.proceed();
	}

}
