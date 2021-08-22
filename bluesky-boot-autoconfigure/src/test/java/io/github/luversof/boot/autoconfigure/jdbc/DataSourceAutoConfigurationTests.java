package io.github.luversof.boot.autoconfigure.jdbc;

import static io.github.luversof.boot.autoconfigure.AutoConfigurationTestInfo.JDBC_CONFIGURATION;
import static io.github.luversof.boot.autoconfigure.AutoConfigurationTestInfo.JDBC_USER_CONFIGURATION;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Random;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class DataSourceAutoConfigurationTests {
	
	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(JDBC_CONFIGURATION))
			.withUserConfiguration(JDBC_USER_CONFIGURATION)
			.withPropertyValues("spring.datasource.initialization-mode=never",
					"spring.datasource.url:jdbc:hsqldb:mem:testdb-" + new Random().nextInt());
	
	@Test
	void testDefaultDataSourceExists() {
		this.contextRunner.run((context) -> assertThat(context).hasSingleBean(DataSource.class));
	}
	
//	@Test
//	void testDataSourceHasEmbeddedDefault() {
//		this.contextRunner.run((context) -> {
//			HikariDataSource dataSource = context.getBean(HikariDataSource.class);
//			assertThat(dataSource.getJdbcUrl()).isNotNull();
//			assertThat(dataSource.getDriverClassName()).isNotNull();
//		});
//	}
	
}
