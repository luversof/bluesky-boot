package io.github.luversof.boot.jdbc.datasource.lookup;

import io.github.luversof.boot.jdbc.datasource.context.RoutingDataSourceContextHolder;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class RoutingDataSource extends AbstractRoutingDataSource {

    @Override
    protected Object determineCurrentLookupKey() {
        return RoutingDataSourceContextHolder.getContext().getLookupKey();
    }
}
