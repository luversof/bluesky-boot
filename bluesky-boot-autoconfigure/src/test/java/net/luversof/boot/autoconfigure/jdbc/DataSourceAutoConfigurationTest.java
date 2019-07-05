package net.luversof.boot.autoconfigure.jdbc;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataSourceAutoConfigurationTest {
	
	@Value("${datasource.default.url}")
	String test;
	
	
	@Test
	public void test() {
		log.debug("test : {}", test);
		log.debug("test : {}", System.getProperty("java.version"));
		
	}
	
	@Configuration
	protected static class Config {

	}
}
