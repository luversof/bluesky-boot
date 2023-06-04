package io.github.luversof.boot.autoconfigure.connectioninfo;

import static io.github.luversof.boot.autoconfigure.AutoConfigurationTestInfo.BASE_PROPERTY;
import static io.github.luversof.boot.autoconfigure.AutoConfigurationTestInfo.CORE_USER_CONFIGURATION;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.TestPropertySources;

import io.github.luversof.boot.autoconfigure.core.config.CoreProperties;
import io.github.luversof.boot.connectioninfo.ConnectionInfoLoaderProperties;
import io.github.luversof.boot.connectioninfo.ConnectionInfoLoaderProperties.LoaderInfo;
import io.github.luversof.boot.connectioninfo.MariadbDataSourceConnectionInfoLoader;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class ConnectionInfoTests {
	
	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withPropertyValues(BASE_PROPERTY)
			.withPropertyValues("bluesky-modules.core.modules.test.domain.web=http://localhost")
			.withPropertyValues("bluesky-modules.core.modules.test.core-module-info=T(io.github.luversof.boot.autoconfigure.core.constant.TestCoreModuleInfo).TEST")
			.withUserConfiguration(CORE_USER_CONFIGURATION)
			.withUserConfiguration(ConnectionInfoAutoConfiguration.class)
			.withUserConfiguration(ConnectionInfoTestConfiguration.class)
			;
	
	@Test
//	@Disabled
	void dataSourceConnectionInfoMariadbLoaderTest() {
		var connectionInfoProperties = new ConnectionInfoLoaderProperties();
		connectionInfoProperties.setLoaders(Map.of("mariadb-datasource", 
				LoaderInfo.builder().properties(Map.of(
						"url", "jdbc:mariadb://172.20.113.197/connection_info",
						"username", "root",
						"password", "root"
						))
				.connections(Map.of("test", List.of("test1"))).build()));
		
		var dataSourceConnectionInfoMariadbLoader = new MariadbDataSourceConnectionInfoLoader(connectionInfoProperties);
		var dataSourceMap = dataSourceConnectionInfoMariadbLoader.load();
		
		log.debug("dataSourceMap : {}", dataSourceMap);
		
		
		var jdbcTemplate = new JdbcTemplate(dataSourceMap.getConnectionInfoMap().get("test1"));
		var connectionInfo = jdbcTemplate.queryForList("SELECT * FROM ConnectionInfo");
		log.debug("connectionInfo : {}", connectionInfo);
		
	}
	
	
	@Test
	void test() {
		this.contextRunner.run(context -> {
			assertThat(context).hasSingleBean(ConnectionInfoLoaderProperties.class);
			var connectionInfoLoaderProperties = context.getBean(ConnectionInfoLoaderProperties.class);
			
			log.debug("connectionInfoLoaderProperties : {}", connectionInfoLoaderProperties);
		});
	}
	
	@Configuration
	@PropertySource("classpath:connectioninfo/connectioninfo.properties")	
	static class ConnectionInfoTestConfiguration {
		
	}

}
