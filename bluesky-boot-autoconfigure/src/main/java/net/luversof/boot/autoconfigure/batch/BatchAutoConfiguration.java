package net.luversof.boot.autoconfigure.batch;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcOperations;


@Configuration(value = "_blueskyBootBatchAutoConfiguration", proxyBeanMethods = false)
@ConditionalOnClass({ JobLauncher.class, DataSource.class, JdbcOperations.class })
@EnableBatchProcessing
public class BatchAutoConfiguration {

}
