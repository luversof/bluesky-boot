package io.github.luversof.boot.autoconfigure.jdbc;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.lang.Nullable;

import io.github.luversof.boot.autoconfigure.jdbc.controller.DataSourceDevCheckController;
import io.github.luversof.boot.connectioninfo.ConnectionInfoCollector;
import io.github.luversof.boot.jdbc.datasource.aspect.RoutingDataSourceAspect;
import io.github.luversof.boot.jdbc.datasource.lookup.RoutingDataSource;
import lombok.extern.slf4j.Slf4j;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for DataSource support.
 * @author bluesky
 *
 */
@Slf4j
@AutoConfiguration("_blueskyBootDataSourceAutoConfiguration")
@EnableConfigurationProperties(DataSourceProperties.class)
@ConditionalOnClass({ DataSource.class, EmbeddedDatabaseType.class })
@PropertySource(value = "classpath:jdbc/jdbc.properties", ignoreResourceNotFound = true)
@PropertySource(value = "classpath:jdbc/jdbc-${bluesky-boot-profile}.properties", ignoreResourceNotFound = true)
@ConditionalOnProperty(prefix = "bluesky-modules.datasource", name = "enabled", havingValue = "true")
public class DataSourceAutoConfiguration {

//    @Bean
//    @Primary
//    @ConfigurationProperties("datasource.default")
//    DataSourceProperties defaultDataSourceProperties() {
//        return new DataSourceProperties();
//    }
//
//    @Bean
//    @Primary
//    DataSource defaultDataSource(@Qualifier("defaultDataSourceProperties") DataSourceProperties defaultDataSourceProperties) {
//        return defaultDataSourceProperties.initializeDataSourceBuilder().type(SimpleDriverDataSource.class).build();
//    }
    
    @Bean
    @Primary
    <T extends DataSource> DataSource routingDataSource(DataSourceProperties dataSourceProperties, @Nullable Map<String, T> dataSourceMap, @Nullable Map<String, ConnectionInfoCollector<T>> connectionInfoCollectorMap) {
    	Map<Object, Object> targetDataSourceMap = new HashMap<>();
    	if (dataSourceMap != null) {
    		targetDataSourceMap.putAll(dataSourceMap);
    	}
    	if (connectionInfoCollectorMap != null) {
    		connectionInfoCollectorMap.forEach((name, connectionInfoCollector) -> {
    			log.debug("The connectionInfoCollector {} is added to the into the blueskyRoutingDataSource", name);
    			targetDataSourceMap.putAll(connectionInfoCollector.getConnectionInfoMap());
    		});
    	}
    	
    	var routingDataSource = new RoutingDataSource();
    	routingDataSource.setTargetDataSources(targetDataSourceMap);
    	// defaultDataSource를 지정하지 않은 경우 아무 값이나 설정
    	if (dataSourceProperties.getDefaultDatasource() == null) {
    		routingDataSource.setDefaultTargetDataSource(targetDataSourceMap.values().stream().findAny().get());
    	} else {
    		routingDataSource.setDefaultTargetDataSource(targetDataSourceMap.get(dataSourceProperties.getDefaultDatasource()));
    	}
    	routingDataSource.afterPropertiesSet();
    	return new LazyConnectionDataSourceProxy(routingDataSource);
    }
    
    @Bean
    RoutingDataSourceAspect routingDataSourceAspect(ApplicationContext applicationContext) {
    	return new RoutingDataSourceAspect(applicationContext);
    }
    
    @Bean
    DataSourceDevCheckController dataSourceDevCheckController(@Qualifier("routingDataSource") DataSource blueskyRoutingDataSource) {
    	return new DataSourceDevCheckController(blueskyRoutingDataSource);
    }

}
