package io.github.luversof.boot.connectioninfo;

import java.sql.Driver;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import lombok.Getter;

public class MariadbDataSourceConnectionInfoLoader extends AbstractDBDataSourceConnectionInfoLoader<HikariDataSource> {
	
	@Getter
	protected String loaderKey = "mariadb-datasource";
	
	public MariadbDataSourceConnectionInfoLoader(ConnectionInfoLoaderProperties connectionInfoProperties) {
		super(connectionInfoProperties);
	}

	@Override
	protected Driver getLoaderDriver() {
		return new org.mariadb.jdbc.Driver();
	}
	
	protected HikariDataSource createDataSource(ConnectionInfo connectionInfo) {
		var config = new HikariConfig();
		config.setJdbcUrl(connectionInfo.getUrl());
		config.setUsername(connectionInfo.getUsername());
		config.setPassword(connectionInfo.getPassword());
		return new HikariDataSource(config);
	}

}
