package io.github.luversof.boot.autoconfigure.jdbc.servlet;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.lang.Nullable;

import io.github.luversof.boot.jdbc.datasource.filter.RoutingDataSourceContextHolderFilter;
import io.github.luversof.boot.jdbc.datasource.support.CommonRoutingDataSourceLookupKeyResolver;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for DataSource Servlet support.
 * @author bluesky
 *
 */
@AutoConfiguration
@ConditionalOnClass({ DataSource.class, EmbeddedDatabaseType.class })
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnProperty(prefix = "bluesky-boot.datasource", name = "enabled", havingValue = "true", matchIfMissing = true)
public class DataSourceMvcAutoConfiguration {

	@Bean
	RoutingDataSourceContextHolderFilter routingDataSourceContextHolderFilter(@Nullable CommonRoutingDataSourceLookupKeyResolver routingDataSourceLookupKeyResolver) {
		return new RoutingDataSourceContextHolderFilter(routingDataSourceLookupKeyResolver);
	}
	
}
