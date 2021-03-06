package net.luversof.boot.autoconfigure.context;
import static net.luversof.boot.autoconfigure.AutoConfigurationTestInfo.BASE_PROPERTY;
import static net.luversof.boot.autoconfigure.AutoConfigurationTestInfo.CORE_MESSAGESOURCE_CONFIGURATION;
import static net.luversof.boot.autoconfigure.AutoConfigurationTestInfo.CORE_MESSAGESOURCE_USER_CONFIGURATION;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

class MessageSourceAutoConfigurationTests {
	
	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withPropertyValues(BASE_PROPERTY)
			.withConfiguration(
					AutoConfigurations.of(CORE_MESSAGESOURCE_CONFIGURATION))
			.withUserConfiguration(CORE_MESSAGESOURCE_USER_CONFIGURATION)
			.withUserConfiguration(Config.class);
	
	@Test
	void testDefaultMessageSource() {
		this.contextRunner.run((context) -> assertThat(
				context.getMessage("messageKey", null, "Foo message", Locale.UK))
						.isEqualTo("messageValue"));
	}
	
	@Test
	void testExtensionMessageSource() {
		this.contextRunner.run((context) -> assertThat(
				context.getMessage("messageExtensionKey", null, "Foo message", Locale.UK))
						.isEqualTo("messageExtensionValue"));
	}
	
	
	@Configuration
	@PropertySource("classpath:context/messageTest.properties")
	protected static class Config {

	}
}
