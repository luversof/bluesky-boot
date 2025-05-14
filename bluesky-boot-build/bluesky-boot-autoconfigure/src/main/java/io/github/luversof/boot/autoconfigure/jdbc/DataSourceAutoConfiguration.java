package io.github.luversof.boot.autoconfigure.jdbc;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.aspectj.weaver.Advice;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.lang.Nullable;

import io.github.luversof.boot.connectioninfo.ConnectionInfoLoader;
import io.github.luversof.boot.connectioninfo.ConnectionInfoRegistry;
import io.github.luversof.boot.jdbc.datasource.aspect.RoutingDataSourceAspect;
import io.github.luversof.boot.jdbc.datasource.controller.DataSourceDevCheckController;
import io.github.luversof.boot.jdbc.datasource.lookup.LazyLoadRoutingDataSource;
import io.github.luversof.boot.jdbc.datasource.lookup.RoutingDataSource;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for DataSource support.
 * @author bluesky
 *
 */
@Slf4j
@AutoConfiguration(value = "blueskyBootDataSourceAutoConfiguration", before = org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class)
@EnableConfigurationProperties(DataSourceProperties.class)
@ConditionalOnClass({ DataSource.class, EmbeddedDatabaseType.class })
@PropertySource(value = "classpath:jdbc/jdbc.properties", ignoreResourceNotFound = true)
@PropertySource(value = "classpath:jdbc/jdbc-${bluesky-boot-profile}.properties", ignoreResourceNotFound = true)
@ConditionalOnProperty(prefix = "bluesky-boot.datasource", name = "enabled", havingValue = "true", matchIfMissing = true)
public class DataSourceAutoConfiguration {
	
	@Configuration(proxyBeanMethods = false)
	@ConditionalOnMissingClass("io.github.luversof.boot.connectioninfo.ConnectionInfoRegistry")
	static class BasicDataSourceAutoConfiguration {
		
		@Bean
		@Primary
		<T extends DataSource> DataSource routingDataSource(
				DataSourceProperties dataSourceProperties,
				@Nullable Map<String, T> dataSourceMap) {
			Map<Object, Object> targetDataSourceMap = new HashMap<>();
			if (dataSourceMap != null) {
				targetDataSourceMap.putAll(dataSourceMap);
			}

			var routingDataSource = new RoutingDataSource();
			routingDataSource.setTargetDataSources(targetDataSourceMap);
			// defaultDataSource를 지정하지 않은 경우 첫번째 값 설정
			if (dataSourceProperties.getDefaultDatasource() == null && !targetDataSourceMap.isEmpty()) {
				routingDataSource.setDefaultTargetDataSource(targetDataSourceMap.values().toArray()[0]);
			} else {
				routingDataSource.setDefaultTargetDataSource(targetDataSourceMap.get(dataSourceProperties.getDefaultDatasource()));
			}
			routingDataSource.afterPropertiesSet();
			return new LazyConnectionDataSourceProxy(routingDataSource);
		}
	
	}
	
	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass(ConnectionInfoRegistry.class)
	static class ConnectionInfoDataSourceAutoConfiguration {
		
		@Bean
		@Primary
		<T extends DataSource> DataSource routingDataSource(
				DataSourceProperties dataSourceProperties,
				@Nullable Map<String, T> dataSourceMap,
				@Nullable ConnectionInfoRegistry<T> connectionInfoRegistry,
				@Nullable Map<String, ConnectionInfoLoader<T>> connectionInfoLoaderMap) {
			Map<Object, Object> targetDataSourceMap = new HashMap<>();
			if (dataSourceMap != null) {
				targetDataSourceMap.putAll(dataSourceMap);
			}
			if (connectionInfoRegistry != null) {
				connectionInfoRegistry.getConnectionInfoList().forEach(connectionInfo -> {
					log.debug("The connectionInfoRegistry {} is added to the into the blueskyRoutingDataSource", connectionInfo.getKey().connectionKey());
					targetDataSourceMap.put(connectionInfo.getKey().connectionKey(), connectionInfo.getConnection());
				});
			}

			var routingDataSource = dataSourceProperties.isUseLazyLoadRoutingDataSource()
					? new LazyLoadRoutingDataSource<>(connectionInfoLoaderMap)
					: new RoutingDataSource();

			routingDataSource.setTargetDataSources(targetDataSourceMap);
			// defaultDataSource를 지정하지 않은 경우 첫번째 값 설정
			if (dataSourceProperties.getDefaultDatasource() == null && !targetDataSourceMap.isEmpty()) {
				routingDataSource.setDefaultTargetDataSource(targetDataSourceMap.values().toArray()[0]);
			} else {
				routingDataSource.setDefaultTargetDataSource(targetDataSourceMap.get(dataSourceProperties.getDefaultDatasource()));
			}
			routingDataSource.afterPropertiesSet();
			return new LazyConnectionDataSourceProxy(routingDataSource);
		}
		
	}
	
	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass(Advice.class)
	static class AspectJDataSourceAutoConfiguration {

		@Bean
		RoutingDataSourceAspect routingDataSourceAspect(ApplicationContext applicationContext) {
			return new RoutingDataSourceAspect(applicationContext);
		}

	}

	@Bean
	DataSourceDevCheckController dataSourceDevCheckController(@Qualifier("routingDataSource") DataSource blueskyRoutingDataSource) {
		return new DataSourceDevCheckController(blueskyRoutingDataSource);
	}

}
