package io.github.luversof.boot.jdbc.datasource.support;

/**
 * routingDataSource 분기 기준 키 설정
 * @author bluesky
 *
 */
@FunctionalInterface
public interface RoutingDataSourceLookupKeyResolver {

	String getLookupKey();

}
