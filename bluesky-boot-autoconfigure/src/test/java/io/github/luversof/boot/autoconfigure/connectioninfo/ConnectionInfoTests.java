package io.github.luversof.boot.autoconfigure.connectioninfo;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import io.github.luversof.boot.connectioninfo.ConnectionInfoProperties;
import io.github.luversof.boot.connectioninfo.ConnectionInfoProperties.LoaderInfo;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class ConnectionInfoTests {
	
	@Test
	void dataSourceConnectionInfoMariadbLoaderTest() {
		var connectionInfoProperties = new ConnectionInfoProperties();
		connectionInfoProperties.setLoaders(Map.of("datasource-mariadb", 
				LoaderInfo.builder().properties(Map.of(
						"url", "jdbc:mariadb://127.0.0.1:3306/spring_config",
						"username", "",
						"password", ""
						))
				.connections(Map.of("test", List.of("test"))).build()));
		
		var dataSourceConnectionInfoMariadbLoader = new DataSourceConnectionInfoMariadbLoader(connectionInfoProperties);
		var dataSourceMap = dataSourceConnectionInfoMariadbLoader.load();
		
		log.debug("dataSourceMap : {}", dataSourceMap);
		
		
		var jdbcTemplate = new JdbcTemplate(dataSourceMap.get("test"));
		var connectionInfo = jdbcTemplate.queryForList("SELECT * FROM ConnectionInfo");
		log.debug("connectionInfo : {}", connectionInfo);
		
	}

}
