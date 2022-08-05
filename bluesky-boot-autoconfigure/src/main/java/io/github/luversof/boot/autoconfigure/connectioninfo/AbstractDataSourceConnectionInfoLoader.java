package io.github.luversof.boot.autoconfigure.connectioninfo;

import java.sql.Driver;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.util.CollectionUtils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.github.luversof.boot.connectioninfo.ConnectionInfoLoader;
import io.github.luversof.boot.connectioninfo.ConnectionInfoProperties;
import io.github.luversof.boot.connectioninfo.ConnectionInfoProperties.LoaderInfo;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class AbstractDataSourceConnectionInfoLoader implements ConnectionInfoLoader<DataSource> {
	
	protected final ConnectionInfoProperties connectionInfoProperties;
	
	@Getter
	protected String loaderQuery = """
		SELECT connection, url, username, password, extradata 
		FROM connectionInfo
		WHERE connection IN ({0})
		""";
	
	protected AbstractDataSourceConnectionInfoLoader(ConnectionInfoProperties connectionInfoProperties) {
		this.connectionInfoProperties = connectionInfoProperties;
	}

	@Override
	public Map<String, DataSource> load() {

		List<String> connectionList = getLoaderInfo().getConnections().values().stream().flatMap(List::stream).distinct().toList();
		
		var loaderJdbcTemplate = getJdbcTemplate();
		
		String sql = MessageFormat.format(getLoaderQuery(), String.join(",", Collections.nCopies(connectionList.size(), "?")));
		
		List<ConnectionInfo> connectionInfoList = loaderJdbcTemplate.query(sql, new ArgumentPreparedStatementSetter(connectionList.toArray()), new BeanPropertyRowMapper<ConnectionInfo>(ConnectionInfo.class));
		
		connectionList.forEach(connection -> {
			if (connectionInfoList.stream().anyMatch(connetionInfo -> connetionInfo.getConnection().equalsIgnoreCase(connection))) {
				log.debug("find database connection ({})", connection);
			} else {
				log.debug("cannot find database connection ({})", connection);
			}
		});
		
		if (CollectionUtils.isEmpty(connectionInfoList)) {
			return Collections.emptyMap();
		}
		
		var dataSourceMap = new HashMap<String, DataSource>();
		
		for (var connectionInfo : connectionInfoList) {
			dataSourceMap.put(connectionInfo.getConnection(), createDataSource(connectionInfo));
		}
		
		return dataSourceMap;
	}

	private JdbcTemplate getJdbcTemplate() {
		var loaderProperties = getLoaderInfo().getProperties();
		String url = loaderProperties.get("url");
		String username = loaderProperties.get("username");
		String password = loaderProperties.get("password");
		
		return new JdbcTemplate(new SimpleDriverDataSource(getLoaderDriver(), url, username, password));
	}

	private LoaderInfo getLoaderInfo() {
		return connectionInfoProperties.getLoaders().get(getLoaderKey());
	}
	
	private DataSource createDataSource(ConnectionInfo connectionInfo) {
		var config = new HikariConfig();
		config.setJdbcUrl(connectionInfo.getUrl());
		config.setUsername(connectionInfo.getUsername());
		config.setPassword(connectionInfo.getPassword());
		return new HikariDataSource(config);
	}

	protected abstract String getLoaderKey();

	protected abstract Driver getLoaderDriver();
	
	@Data
	public static class ConnectionInfo {
		private String connection;
		private String url;
		private String username;
		private String password;
		private String extradata;
	}

}
