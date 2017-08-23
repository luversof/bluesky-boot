package net.luversof.boot.autoconfigure.jdbc;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

@Configuration("_blueskyBootDataSourceAutoConfiguration")
@ConditionalOnClass({ DataSource.class, EmbeddedDatabaseType.class })
@PropertySource("classpath:jdbc/jdbc.properties")
@PropertySource("classpath:jdbc/jdbc-${spring.profiles.active}.properties")
public class DataSourceAutoConfiguration {

}
