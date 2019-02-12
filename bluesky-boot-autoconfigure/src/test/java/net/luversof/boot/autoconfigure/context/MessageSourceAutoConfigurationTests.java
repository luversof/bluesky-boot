package net.luversof.boot.autoconfigure.context;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;

import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

public class MessageSourceAutoConfigurationTests {
	
	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withPropertyValues("net-profile:opdev")
			.withConfiguration(
					AutoConfigurations.of(org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration.class,
							Config.class,
							MessageSourceAutoConfiguration.class));
	
	@Test
	public void testDefaultMessageSource() {
		this.contextRunner.run((context) -> assertThat(
				context.getMessage("messageKey", null, "Foo message", Locale.UK))
						.isEqualTo("messageValue"));
	}
	
	@Test
	public void testExtensionMessageSource() {
		this.contextRunner.run((context) -> assertThat(
				context.getMessage("messageExtensionKey", null, "Foo message", Locale.UK))
						.isEqualTo("messageExtensionValue"));
	}
	
	
	@Configuration
	@PropertySource("classpath:messageTest.properties")
	protected static class Config {

	}
}
