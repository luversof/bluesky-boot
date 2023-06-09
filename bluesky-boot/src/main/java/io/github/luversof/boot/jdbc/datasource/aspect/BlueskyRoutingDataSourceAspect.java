package io.github.luversof.boot.jdbc.datasource.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import io.github.luversof.boot.jdbc.datasource.annotation.BlueskyRoutingDataSource;
import io.github.luversof.boot.jdbc.datasource.context.BlueskyRoutingDataSourceContextHolder;

@Aspect
public class BlueskyRoutingDataSourceAspect {
	
	@Around("@within(blueskyRoutingDataSource)")
	public Object classAround(ProceedingJoinPoint proceedingJoinPoint, BlueskyRoutingDataSource blueskyRoutingDataSource) throws Throwable {
		return execute(proceedingJoinPoint, blueskyRoutingDataSource);
	}
	
	@Around("@annotation(blueskyRoutingDataSource)")
	public Object methodAround(ProceedingJoinPoint proceedingJoinPoint, BlueskyRoutingDataSource blueskyRoutingDataSource) throws Throwable {
		return execute(proceedingJoinPoint, blueskyRoutingDataSource);
	}
	
	private Object execute(ProceedingJoinPoint proceedingJoinPoint, BlueskyRoutingDataSource blueskyRoutingDataSource) throws Throwable {
		BlueskyRoutingDataSourceContextHolder.setContext(blueskyRoutingDataSource::value);
		return proceedingJoinPoint.proceed();
	}
}
