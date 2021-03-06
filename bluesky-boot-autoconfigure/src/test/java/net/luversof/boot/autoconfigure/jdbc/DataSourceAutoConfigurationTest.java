package net.luversof.boot.autoconfigure.jdbc;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class DataSourceAutoConfigurationTest {
	
	@Value("${datasource.default.url}")
	String test;
	
	
	@Test
	void test() {
		log.debug("test : {}", test);
		log.debug("test : {}", System.getProperty("java.version"));
		assertThat(System.getProperty("java.version")).isNotNull();
	}
	
	@Configuration
	protected static class Config {

	}
}
