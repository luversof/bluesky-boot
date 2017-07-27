package net.luversof.boot.autoconfigure.context;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import net.luversof.boot.autoconfigure.context.BlueskyMessageSourceAutoConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest
@ImportAutoConfiguration(BlueskyMessageSourceAutoConfiguration.class)
@DirtiesContext
public class BlueskyMessageSourceAutoConfigurationTest {
	
	@Autowired
	private ApplicationContext context;
	
	@Test
	public void test() {
		assertThat(this.context.getMessage("messageKey", null, "Foo message", Locale.UK)).isEqualTo("messageValue");
	}
	
	@Configuration
	protected static class Config {

	}
}
