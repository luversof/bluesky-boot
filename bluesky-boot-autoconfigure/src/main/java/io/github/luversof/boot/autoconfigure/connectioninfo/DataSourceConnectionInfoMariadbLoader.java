package io.github.luversof.boot.autoconfigure.connectioninfo;

import java.sql.Driver;

import io.github.luversof.boot.connectioninfo.ConnectionInfoProperties;
import lombok.Getter;

public class DataSourceConnectionInfoMariadbLoader extends AbstractDataSourceConnectionInfoLoader {
	
	@Getter
	protected String loaderKey = "datasource-mariadb";
	
	public DataSourceConnectionInfoMariadbLoader(ConnectionInfoProperties connectionInfoProperties) {
		super(connectionInfoProperties);
	}

	@Override
	protected Driver getLoaderDriver() {
		return new org.mariadb.jdbc.Driver();
	}

}
