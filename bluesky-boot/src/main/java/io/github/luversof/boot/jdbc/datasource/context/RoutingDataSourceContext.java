package io.github.luversof.boot.jdbc.datasource.context;

@FunctionalInterface
public interface RoutingDataSourceContext {

	String getLookupKey();

}
