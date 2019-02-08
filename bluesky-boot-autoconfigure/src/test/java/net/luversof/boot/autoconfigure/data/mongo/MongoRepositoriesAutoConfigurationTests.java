package net.luversof.boot.autoconfigure.data.mongo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.util.ReflectionTestUtils;

import com.mongodb.MongoClient;

import net.luversof.boot.autoconfigure.TestAutoConfigurationPackage;
import net.luversof.boot.autoconfigure.data.mongo.city.City;
import net.luversof.boot.autoconfigure.data.mongo.city.CityRepository;

public class MongoRepositoriesAutoConfigurationTests {
	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withPropertyValues("net-profile:opdev")
			.withConfiguration(AutoConfigurations.of(MongoAutoConfiguration.class,
					org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration.class,
					MongoRepositoriesAutoConfiguration.class,
					PropertyPlaceholderAutoConfiguration.class,
					MongoDataAutoConfiguration.class));
	
	@Test
	public void testDefaultRepositoryConfiguration() {
		this.contextRunner.withUserConfiguration(TestConfiguration.class)
				.run((context) -> {
					assertThat(context).hasSingleBean(CityRepository.class);
					assertThat(context).hasSingleBean(MongoClient.class);
					MongoMappingContext mappingContext = context
							.getBean(MongoMappingContext.class);
					@SuppressWarnings("unchecked")
					Set<? extends Class<?>> entities = (Set<? extends Class<?>>) ReflectionTestUtils
							.getField(mappingContext, "initialEntitySet");
					assertThat(entities).hasSize(1);
				});
	}
	
	@Test
	public void doesNotTriggerDefaultRepositoryDetectionIfCustomized() {
		this.contextRunner.withUserConfiguration(CustomizedConfiguration.class)
				.run((context) -> assertThat(context)
						.hasSingleBean(CityRepository.class));
	}
	
	@Configuration
	@TestAutoConfigurationPackage(City.class)
	protected static class TestConfiguration {

	}
	
	@Configuration
	@TestAutoConfigurationPackage(MongoRepositoriesAutoConfigurationTests.class)
	@EnableMongoRepositories(basePackageClasses = CityRepository.class)
	protected static class CustomizedConfiguration {

	}
}
