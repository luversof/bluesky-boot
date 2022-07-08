package io.github.luversof.boot.autoconfigure.batch;

import javax.sql.DataSource;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.jdbc.core.JdbcOperations;


@AutoConfiguration("_blueskyBootBatchAutoConfiguration")
@ConditionalOnClass({ JobLauncher.class, DataSource.class, JdbcOperations.class })
@EnableBatchProcessing
public class BatchAutoConfiguration {

}
