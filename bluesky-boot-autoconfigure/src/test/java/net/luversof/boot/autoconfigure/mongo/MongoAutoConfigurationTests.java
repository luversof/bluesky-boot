package net.luversof.boot.autoconfigure.mongo;

import static net.luversof.boot.autoconfigure.AutoConfigurationTestInfo.BASE_PROPERTY;
import static net.luversof.boot.autoconfigure.AutoConfigurationTestInfo.CORE_USER_CONFIGURATION;
import static net.luversof.boot.autoconfigure.AutoConfigurationTestInfo.MONGO_CONFIGURATION;
import static net.luversof.boot.autoconfigure.AutoConfigurationTestInfo.MONGO_USER_CONFIGURATION;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.mongodb.client.MongoClient;

import net.luversof.boot.autoconfigure.mongo.config.MongoProperties;

public class MongoAutoConfigurationTests {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withPropertyValues(BASE_PROPERTY)
			.withConfiguration(AutoConfigurations.of(MONGO_CONFIGURATION))
			.withUserConfiguration(MONGO_USER_CONFIGURATION)
			.withUserConfiguration(Config.class);
	
	
	@Test
	public void clientExists() {
		this.contextRunner.run(context -> {
			assertThat(context).hasSingleBean(MongoClient.class);
		});
	}
	
	@Test
	public void mongoProperties() {
		this.contextRunner.run(context -> {
			assertThat(context).hasSingleBean(MongoProperties.class);
			var mongoProperties = context.getBean(MongoProperties.class);
			assertThat(mongoProperties.getConnectionMap().get("test1").getPort()).isEqualTo(27017);
		});
	}
	
	@Configuration(proxyBeanMethods =  false)
	@PropertySource(value = "classpath:mongo/mongo-${net-profile}.properties", ignoreResourceNotFound = true)
	public static class Config {
		
	}
			
}
