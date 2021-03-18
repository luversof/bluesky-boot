package io.github.luversof.boot.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

@DirtiesContext
@SpringBootTest
class SpringJUnitTests {

	@Autowired
	private ApplicationContext context;
	
	@Test
	void testContextCreated() {
		assertThat(this.context).isNotNull();
	}
	
	@Configuration(proxyBeanMethods = false)
	@Import({ PropertyPlaceholderAutoConfiguration.class })
	static class TestConfiguration {
		
	}
}
