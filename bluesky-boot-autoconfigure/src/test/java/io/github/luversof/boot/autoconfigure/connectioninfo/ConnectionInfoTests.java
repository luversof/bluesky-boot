package io.github.luversof.boot.autoconfigure.connectioninfo;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import io.github.luversof.boot.connectioninfo.ConnectionInfoLoaderProperties;
import io.github.luversof.boot.connectioninfo.ConnectionInfoLoaderProperties.LoaderInfo;
import io.github.luversof.boot.connectioninfo.MariadbDataSourceConnectionInfoLoader;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class ConnectionInfoTests {
	
	@Test
	@Disabled
	void dataSourceConnectionInfoMariadbLoaderTest() {
		var connectionInfoProperties = new ConnectionInfoLoaderProperties();
		connectionInfoProperties.setLoaders(Map.of("mariadb-datasource", 
				LoaderInfo.builder().properties(Map.of(
						"url", "jdbc:mariadb://127.0.0.1:3306/spring_config",
						"username", "",
						"password", ""
						))
				.connections(Map.of("test", List.of("test"))).build()));
		
		var dataSourceConnectionInfoMariadbLoader = new MariadbDataSourceConnectionInfoLoader(connectionInfoProperties);
		var dataSourceMap = dataSourceConnectionInfoMariadbLoader.load();
		
		log.debug("dataSourceMap : {}", dataSourceMap);
		
		
		var jdbcTemplate = new JdbcTemplate(dataSourceMap.getConnectionInfoMap().get("test"));
		var connectionInfo = jdbcTemplate.queryForList("SELECT * FROM ConnectionInfo");
		log.debug("connectionInfo : {}", connectionInfo);
		
	}

}
