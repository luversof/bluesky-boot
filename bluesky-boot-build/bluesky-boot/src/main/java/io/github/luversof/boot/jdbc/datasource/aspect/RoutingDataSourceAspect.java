package io.github.luversof.boot.jdbc.datasource.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import io.github.luversof.boot.jdbc.datasource.annotation.RoutingDataSource;
import io.github.luversof.boot.jdbc.datasource.context.RoutingDataSourceContextHolder;
import io.github.luversof.boot.jdbc.datasource.support.RoutingDataSourceLookupKeyResolver;

@Aspect
public class RoutingDataSourceAspect {

  private ApplicationContext applicationContext;

  public RoutingDataSourceAspect(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @Around("@within(routingDataSource)")
  public Object classAround(
      ProceedingJoinPoint proceedingJoinPoint, RoutingDataSource routingDataSource)
      throws Throwable {
    return execute(proceedingJoinPoint, routingDataSource);
  }

  @Around("@annotation(routingDataSource)")
  public Object methodAround(
      ProceedingJoinPoint proceedingJoinPoint, RoutingDataSource routingDataSource)
      throws Throwable {
    return execute(proceedingJoinPoint, routingDataSource);
  }

  private Object execute(
      ProceedingJoinPoint proceedingJoinPoint, RoutingDataSource routingDataSource)
      throws Throwable {
    // 끝나면 지우지 않고 되돌린다 - 지우면 바깥 호출(또는 필터)의 lookupKey 가 사라져 남은 쿼리가 기본 DB 로 간다
    var previousContext = RoutingDataSourceContextHolder.getContext();

    if (StringUtils.hasText(routingDataSource.resolver())) {
      var resolver =
          applicationContext.getBean(
              routingDataSource.resolver(), RoutingDataSourceLookupKeyResolver.class);
      RoutingDataSourceContextHolder.setContext(resolver::getLookupKey);
    } else if (StringUtils.hasText(routingDataSource.value())) {
      RoutingDataSourceContextHolder.setContext(routingDataSource::value);
    }

    try {
      return proceedingJoinPoint.proceed();
    } finally {
      RoutingDataSourceContextHolder.setContext(previousContext);
    }
  }
}
