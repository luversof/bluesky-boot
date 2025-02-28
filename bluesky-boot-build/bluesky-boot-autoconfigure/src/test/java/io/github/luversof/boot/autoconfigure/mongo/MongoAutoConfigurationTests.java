package io.github.luversof.boot.autoconfigure.mongo;

import static io.github.luversof.boot.autoconfigure.AutoConfigurationTestInfo.BASE_PROPERTY;
import static io.github.luversof.boot.autoconfigure.AutoConfigurationTestInfo.MONGO_CONFIGURATION;
import static io.github.luversof.boot.autoconfigure.AutoConfigurationTestInfo.MONGO_USER_CONFIGURATION;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import com.mongodb.client.MongoClient;

import io.github.luversof.boot.test.context.runner.BlueskyApplicationContextRunner;


@Disabled
class MongoAutoConfigurationTests {

	private final BlueskyApplicationContextRunner contextRunner = new BlueskyApplicationContextRunner()
			.withPropertyValues(BASE_PROPERTY)
			.withConfiguration(AutoConfigurations.of(MONGO_CONFIGURATION))
			.withUserConfiguration(MONGO_USER_CONFIGURATION)
			.withUserConfiguration(Config.class);
	
	
	@Test
	void clientExists() {
		this.contextRunner.run(context -> {
			assertThat(context).hasSingleBean(MongoClient.class);
		});
	}
	
	@Test
	void mongoProperties() {
		this.contextRunner.run(context -> {
			assertThat(context).hasSingleBean(MongoProperties.class);
			var mongoProperties = context.getBean(MongoProperties.class);
			assertThat(mongoProperties.getDefaultMongoProperties().getPort()).isEqualTo(27017);
		});
	}
	
	@Configuration(proxyBeanMethods =  false)
	@PropertySource(value = "classpath:mongodb/mongodb-${bluesky-boot-profile}.properties", ignoreResourceNotFound = true)
	static class Config {

	}
			
}
