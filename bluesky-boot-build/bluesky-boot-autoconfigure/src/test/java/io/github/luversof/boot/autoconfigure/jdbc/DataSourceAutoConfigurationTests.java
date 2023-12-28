package io.github.luversof.boot.autoconfigure.jdbc;

import static io.github.luversof.boot.autoconfigure.AutoConfigurationTestInfo.CORE_USER_CONFIGURATION;
import static io.github.luversof.boot.autoconfigure.AutoConfigurationTestInfo.JDBC_CONFIGURATION;
import static io.github.luversof.boot.autoconfigure.AutoConfigurationTestInfo.JDBC_USER_CONFIGURATION;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Random;

import javax.sql.DataSource;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;

import com.zaxxer.hikari.HikariDataSource;

import io.github.luversof.boot.autoconfigure.connectioninfo.ConnectionInfoAutoConfiguration;
import io.github.luversof.boot.env.ProfileEnvironmentPostProcessor;
import io.github.luversof.boot.jdbc.datasource.lookup.RoutingDataSource;
import io.github.luversof.boot.security.crypto.env.DecryptEnvironmentPostProcessor;

@Disabled
class DataSourceAutoConfigurationTests {
	
	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withInitializer((applicationContext) -> new ProfileEnvironmentPostProcessor().postProcessEnvironment(applicationContext.getEnvironment(), null))
			.withConfiguration(AutoConfigurations.of(JDBC_CONFIGURATION))
			.withUserConfiguration(JDBC_USER_CONFIGURATION)
			.withPropertyValues("spring.datasource.initialization-mode=never",
					"spring.datasource.url:jdbc:hsqldb:mem:testdb-" + new Random().nextInt());
	
	@Test
	void testDefaultDataSourceExists() {
		this.contextRunner.run((context) -> assertThat(context).hasSingleBean(DataSource.class));
	}
	
	@Test
	void testWithConnectionInfo() {
		this.contextRunner
			.withInitializer((applicationContext) -> new DecryptEnvironmentPostProcessor().postProcessEnvironment(applicationContext.getEnvironment(), null))
			.withPropertyValues(
				"bluesky-boot.connection-info.loaders.mariadb-datasource.enabled=true",
				"bluesky-boot.connection-info.loaders.mariadb-datasource.properties.url=jdbc:mariadb://mariadb.bluesky.local:3306/connection_info",
				"bluesky-boot.connection-info.loaders.mariadb-datasource.properties.username={text}dd2d9a9a3735b9f9a63664dca900b04e34d92759a43d301c74dd60d235c9576c",
				"bluesky-boot.connection-info.loaders.mariadb-datasource.properties.password={text}dd2d9a9a3735b9f9a63664dca900b04e34d92759a43d301c74dd60d235c9576c",
				"bluesky-boot.connection-info.loaders.mariadb-datasource.connections.mapexample=test1",
				"bluesky-boot.connection-info.loaders.sqlserver-datasource.enabled=true",
				"bluesky-boot.connection-info.loaders.sqlserver-datasource.properties.url=jdbc:sqlserver://mssql.bluesky.local;encrypt=false;databaseName=connection_info",
				"bluesky-boot.connection-info.loaders.sqlserver-datasource.properties.username={text}6dfa79bdb4311fe011683a2fbf1b281eb6bfe47523575919533e1c0a99986dfa",
				"bluesky-boot.connection-info.loaders.sqlserver-datasource.properties.password={text}cd59e88989c267f8e68e5195fd9e8cc16110118a78f04f14da9f72aa4eda0b85",
				"bluesky-boot.connection-info.loaders.sqlserver-datasource.connections.mapexample=test1"
			)
			.withPropertyValues("bluesky-boot.core.modules.test.domain.web=http://localhost")
			.withPropertyValues("bluesky-boot.core.modules.test.core-module-info=T(io.github.luversof.boot.autoconfigure.core.constant.TestCoreModuleInfo).TEST")
			.withUserConfiguration(CORE_USER_CONFIGURATION)
			.withUserConfiguration(ConnectionInfoAutoConfiguration.class)
			.run((context) -> {
				DataSource dataSource = context.getBean("routingDataSource", DataSource.class);
				assertThat(dataSource).isNotNull();
				if (dataSource instanceof LazyConnectionDataSourceProxy lazyConnectionDataSourceProxy) {
					var targetDataSource = lazyConnectionDataSourceProxy.getTargetDataSource();
					if (targetDataSource instanceof RoutingDataSource routingDataSource) {
						var resolvedDataSources = routingDataSource.getResolvedDataSources();
						var testDataSource = DataSourceBuilder.create(null)
								.type(HikariDataSource.class)
								.build();
						var newMap = new HashMap<Object, Object>();
						newMap.putAll(resolvedDataSources);
						newMap.put("aaa", testDataSource);
						
						routingDataSource.setTargetDataSources(newMap);
						routingDataSource.initialize();
						assertThat(resolvedDataSources).isNotEmpty();
					}
				}
			});
	}
	
}
