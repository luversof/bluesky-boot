package io.github.luversof.boot.connectioninfo.datasource;

import java.sql.Driver;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.github.luversof.boot.connectioninfo.ConnectionInfoLoaderProperties;
import io.github.luversof.boot.security.crypto.factory.TextEncryptorFactories;
import lombok.Getter;
import lombok.Setter;

public class SQLServerDataSourceConnectionInfoLoader extends AbstractDBDataSourceConnectionInfoLoader<HikariDataSource> {
	
	@Getter
	@Setter
	protected String loaderKey = "sqlserver-datasource";
	
	public SQLServerDataSourceConnectionInfoLoader(ConnectionInfoLoaderProperties connectionInfoProperties) {
		super(connectionInfoProperties);
	}

	@Override
	protected Driver getLoaderDriver() {
		return new com.microsoft.sqlserver.jdbc.SQLServerDriver();
	}
	
	protected HikariDataSource createDataSource(ConnectionInfo connectionInfo) {
		var config = new HikariConfig();
		var textEncryptor = TextEncryptorFactories.getDelegatingTextEncryptor();
		config.setJdbcUrl(connectionInfo.url());
		config.setUsername(textEncryptor.decrypt(connectionInfo.username()));
		config.setPassword(textEncryptor.decrypt(connectionInfo.password()));
		return new HikariDataSource(config);
	}

}
