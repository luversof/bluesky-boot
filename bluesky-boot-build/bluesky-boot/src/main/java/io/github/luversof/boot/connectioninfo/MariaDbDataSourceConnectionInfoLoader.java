package io.github.luversof.boot.connectioninfo;

import java.sql.Driver;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import io.github.luversof.boot.security.crypto.factory.TextEncryptorFactories;
import lombok.Getter;
import lombok.Setter;

public class MariaDbDataSourceConnectionInfoLoader extends AbstractDBDataSourceConnectionInfoLoader<HikariDataSource> {
	
	@Getter
	@Setter
	protected String loaderKey = "mariadb-datasource";
	
	public MariaDbDataSourceConnectionInfoLoader(ConnectionInfoLoaderProperties connectionInfoProperties) {
		super(connectionInfoProperties);
	}

	@Override
	protected Driver getLoaderDriver() {
		return new org.mariadb.jdbc.Driver();
	}
	
	protected HikariDataSource createDataSource(ConnectionInfo connectionInfo) {
		var config = new HikariConfig();
		var textEncryptor = TextEncryptorFactories.getDelegatingTextEncryptor();
		config.setJdbcUrl(connectionInfo.getUrl());
		config.setUsername(textEncryptor.decrypt(connectionInfo.getUsername()));
		config.setPassword(textEncryptor.decrypt(connectionInfo.getPassword()));
		return new HikariDataSource(config);
	}

}
