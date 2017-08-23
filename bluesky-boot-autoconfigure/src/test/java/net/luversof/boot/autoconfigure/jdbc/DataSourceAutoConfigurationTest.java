package net.luversof.boot.autoconfigure.jdbc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@ImportAutoConfiguration(DataSourceAutoConfiguration.class)
@DirtiesContext
@Slf4j
public class DataSourceAutoConfigurationTest {
	
	@Value("${datasource.mysql.driver-class-name}")
	String test;
	
	
	@Test
	public void test() {
		log.debug("test : {}", test);
	}
	
	@Configuration
	protected static class Config {

	}
}
