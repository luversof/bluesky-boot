package io.github.luversof.boot.connectioninfo.datasource;

import java.sql.Driver;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.util.CollectionUtils;

import io.github.luversof.boot.connectioninfo.ConnectionInfoCollector;
import io.github.luversof.boot.connectioninfo.ConnectionInfoLoader;
import io.github.luversof.boot.connectioninfo.ConnectionInfoLoaderProperties;
import io.github.luversof.boot.security.crypto.factory.TextEncryptorFactories;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * DB에서 connectionInfo를 load 해서 DataSource를 구하는 경우 사용
 * @author bluesky
 *
 */
@Slf4j
public abstract class AbstractDBDataSourceConnectionInfoLoader<T extends DataSource> implements ConnectionInfoLoader<T> {
	
	protected final ConnectionInfoLoaderProperties connectionInfoLoaderProperties;
	
	@Getter
	protected String loaderQuery = """
		SELECT connection, url, username, password, extradata 
		FROM ConnectionInfo
		WHERE connection IN ({0})
		""";
	
	protected AbstractDBDataSourceConnectionInfoLoader(ConnectionInfoLoaderProperties connectionInfoLoaderProperties) {
		this.connectionInfoLoaderProperties = connectionInfoLoaderProperties;
	}
	
	@Override
	public ConnectionInfoCollector<T> load(List<String> connectionList) {
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
			return Collections::emptyMap;
		}

		var dataSourceMap = new HashMap<String, T>();
		for (var connectionInfo : connectionInfoList) {
			dataSourceMap.put(connectionInfo.getConnection(), createDataSource(connectionInfo));
		}
		
		return () -> dataSourceMap;
		
	}

	@Override
	public ConnectionInfoCollector<T> load() {
		
		if (connectionInfoLoaderProperties == null 
				|| connectionInfoLoaderProperties.getLoaders() == null 
				|| !connectionInfoLoaderProperties.getLoaders().containsKey(getLoaderKey())
				|| CollectionUtils.isEmpty(connectionInfoLoaderProperties.getLoaders().get(getLoaderKey()).getConnections())) {
			return Collections::emptyMap;
		}

		List<String> connectionList = connectionInfoLoaderProperties.getLoaders().get(getLoaderKey()).getConnections().values().stream().flatMap(List::stream).distinct().toList();
		
		return load(connectionList);
	}

	private JdbcTemplate getJdbcTemplate() {
		var encryptor = TextEncryptorFactories.getDelegatingTextEncryptor();
		
		var loaderProperties = connectionInfoLoaderProperties.getLoaders().get(getLoaderKey()).getProperties();
		String url = loaderProperties.get("url");
		String username = encryptor.decrypt(loaderProperties.get("username"));
		String password = encryptor.decrypt(loaderProperties.get("password"));
		
		return new JdbcTemplate(new SimpleDriverDataSource(getLoaderDriver(), url, username, password));
	}

	protected abstract T createDataSource(ConnectionInfo connectionInfo);

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
