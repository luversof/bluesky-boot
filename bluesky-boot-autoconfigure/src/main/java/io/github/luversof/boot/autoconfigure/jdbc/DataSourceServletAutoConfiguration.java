package io.github.luversof.boot.autoconfigure.jdbc;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.lang.Nullable;

import io.github.luversof.boot.jdbc.datasource.filter.RoutingDataSourceContextHolderFilter;
import io.github.luversof.boot.jdbc.datasource.support.CommonRoutingDataSourceLookupKeyResolver;

@AutoConfiguration(value = "_blueskyBootDataSourceServletAutoConfiguration")
@ConditionalOnClass({ DataSource.class, EmbeddedDatabaseType.class })
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnProperty(prefix = "bluesky-modules.datasource", name = "enabled", havingValue = "true")
public class DataSourceServletAutoConfiguration {

	@Bean
	RoutingDataSourceContextHolderFilter routingDataSourceContextHolderFilter(@Nullable CommonRoutingDataSourceLookupKeyResolver routingDataSourceLookupKeyResolver) {
		return new RoutingDataSourceContextHolderFilter(routingDataSourceLookupKeyResolver);
	}
	
}
