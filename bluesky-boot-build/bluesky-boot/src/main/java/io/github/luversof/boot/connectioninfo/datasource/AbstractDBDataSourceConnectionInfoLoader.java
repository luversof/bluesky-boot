package io.github.luversof.boot.connectioninfo.datasource;

import java.sql.Driver;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.ArgumentPreparedStatementSetter;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.util.CollectionUtils;

import io.github.luversof.boot.connectioninfo.ConnectionInfoCollector;
import io.github.luversof.boot.connectioninfo.ConnectionInfoLoader;
import io.github.luversof.boot.connectioninfo.ConnectionInfoLoaderProperties;
import io.github.luversof.boot.security.crypto.factory.TextEncryptorFactories;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Used when obtaining DataSource by loading connectionInfo from DB
 * @author bluesky
 * 
 * @param <T> The type of DataSource to be loaded via Loader
 */
@Slf4j
public abstract class AbstractDBDataSourceConnectionInfoLoader<T extends DataSource> implements ConnectionInfoLoader<T> {
	
	/**
	 * An object containing loader information and a list of connection targets to load.
	 */
	protected final ConnectionInfoLoaderProperties connectionInfoLoaderProperties;
	
	/**
	 * Query to retrieve connection information from loader
	 */
	@Getter
	protected String loaderQuery = """
		SELECT connection, url, username, password, extradata 
		FROM ConnectionInfo
		WHERE connection IN ({0})
		""";
	
	/**
	 * DB's datasource requires connectionInfoLoaderProperties, so pass it to the constructor.
	 * 
	 * @param connectionInfoLoaderProperties An object containing loader information and a list of connection targets to load.
	 */
	protected AbstractDBDataSourceConnectionInfoLoader(ConnectionInfoLoaderProperties connectionInfoLoaderProperties) {
		this.connectionInfoLoaderProperties = connectionInfoLoaderProperties;
	}
	
	@Override
	public ConnectionInfoCollector<T> load(List<String> connectionList) {
		var loaderJdbcTemplate = getJdbcTemplate();
		
		String sql = MessageFormat.format(getLoaderQuery(), String.join(",", Collections.nCopies(connectionList.size(), "?")));
		
		List<ConnectionInfo> connectionInfoList = loaderJdbcTemplate.query(sql, new ArgumentPreparedStatementSetter(connectionList.toArray()), new DataClassRowMapper<ConnectionInfo>(ConnectionInfo.class));
		
		connectionList.forEach(connection -> {
			if (connectionInfoList.stream().anyMatch(connetionInfo -> connetionInfo.connection().equalsIgnoreCase(connection))) {
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
			dataSourceMap.put(connectionInfo.connection(), createDataSource(connectionInfo));
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

	/**
	 * Creating a DataSource for ConnectionInfo
	 * 
	 * @param connectionInfo Connection information obtained through the loader
	 * @return Generated DataSource
	 */
	protected abstract T createDataSource(ConnectionInfo connectionInfo);

	/**
	 * Key to be used among the keys in the ConnectionInfoLoaderProperties loader map
	 * 
	 * @return Target key of loader map
	 */
	protected abstract String getLoaderKey();

	/**
	 * Call the target Driver object to be used by the loader
	 * 
	 * @return Target Driver object
	 */
	protected abstract Driver getLoaderDriver();
	
	/**
	 * Connection information obtained through the loader
	 * 
	 * @param connection connection
	 * @param url url
	 * @param username username
	 * @param password password
	 * @param extradata extradata
	 */
	public static record ConnectionInfo(String connection, String url, String username, String password, String extradata) {
	}

}
