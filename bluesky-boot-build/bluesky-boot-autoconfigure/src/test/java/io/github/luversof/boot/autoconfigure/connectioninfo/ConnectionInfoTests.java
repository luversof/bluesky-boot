package io.github.luversof.boot.autoconfigure.connectioninfo;

import static io.github.luversof.boot.autoconfigure.AutoConfigurationTestInfo.BASE_PROPERTY;
import static io.github.luversof.boot.autoconfigure.AutoConfigurationTestInfo.CORE_USER_CONFIGURATION;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import io.github.luversof.boot.connectioninfo.ConnectionInfoCollector;
import io.github.luversof.boot.connectioninfo.ConnectionInfoLoaderProperties;
import io.github.luversof.boot.connectioninfo.ConnectionInfoLoaderProperties.LoaderInfo;
import io.github.luversof.boot.security.crypto.env.DecryptEnvironmentPostProcessor;
import io.github.luversof.boot.connectioninfo.MariaDbDataSourceConnectionInfoLoader;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ActiveProfiles("localDev")
@Disabled
class ConnectionInfoTests {
	
	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withInitializer((applicationContext) -> new DecryptEnvironmentPostProcessor().postProcessEnvironment(applicationContext.getEnvironment(), null))
			.withPropertyValues(BASE_PROPERTY)
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
			;
	
	@Test
	void dataSourceConnectionInfoMariadbLoaderTest() {
		var connectionInfoProperties = new ConnectionInfoLoaderProperties();
		connectionInfoProperties.setLoaders(Map.of("mariadb-datasource", 
				LoaderInfo.builder().properties(Map.of(
						"url", "jdbc:mariadb://mariadb.bluesky.local:3306/connection_info",
						"username", "root",
						"password", "root"
						))
				.connections(Map.of("test", List.of("test1"))).build()));
		
		var dataSourceConnectionInfoMariadbLoader = new MariaDbDataSourceConnectionInfoLoader(connectionInfoProperties);
		var dataSourceMap = dataSourceConnectionInfoMariadbLoader.load();
		
		log.debug("dataSourceMap : {}", dataSourceMap);
		
		
		var jdbcTemplate = new JdbcTemplate(dataSourceMap.getConnectionInfoMap().get("test1"));
		var connectionInfo = jdbcTemplate.queryForList("SELECT * FROM ConnectionInfo");
		log.debug("connectionInfo : {}", connectionInfo);
		
	}
	
	
	@Test
	void mariaDbDataSourceConnectionInfoLoaderTest() {
		this.contextRunner.run(context -> {
			var beanName = "mariaDbDataSourceConnectionInfoCollector";
			assertThat(context).hasBean(beanName);
			var connectionInfoLoaderProperties = context.getBean(ConnectionInfoLoaderProperties.class);
			
			log.debug("connectionInfoLoaderProperties : {}", connectionInfoLoaderProperties);
			log.debug("connectionInfoLoaderProperties username : {}", connectionInfoLoaderProperties.getLoaders().get("mariadb-datasource").getProperties().get("username"));
			
			var connectionInfoCollector = context.getBean(beanName, ConnectionInfoCollector.class);
			log.debug("connectionInfoCollector : {}", connectionInfoCollector.getConnectionInfoMap());
		});
	}
	
	@Test
	void sqlServerDataSourceConnectionInfoLoaderTest() {
		this.contextRunner.run(context -> {
			var beanName = "sqlServerDataSourceConnectionInfoCollector";
			assertThat(context).hasBean(beanName);
			var connectionInfoLoaderProperties = context.getBean(ConnectionInfoLoaderProperties.class);
			
			log.debug("connectionInfoLoaderProperties : {}", connectionInfoLoaderProperties);
			log.debug("connectionInfoLoaderProperties username : {}", connectionInfoLoaderProperties.getLoaders().get("sqlserver-datasource").getProperties().get("username"));
			
			var connectionInfoCollector = context.getBean(beanName, ConnectionInfoCollector.class);
			log.debug("connectionInfoCollector : {}", connectionInfoCollector.getConnectionInfoMap());
		});
	}
	
	
	
}
