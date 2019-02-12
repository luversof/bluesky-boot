package net.luversof.boot.autoconfigure.data.mongo;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoReactiveAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.test.util.ReflectionTestUtils;

import com.mongodb.reactivestreams.client.MongoClient;

import net.luversof.boot.autoconfigure.TestAutoConfigurationPackage;
import net.luversof.boot.autoconfigure.data.alt.mongo.CityMongoDbRepository;
import net.luversof.boot.autoconfigure.data.alt.mongo.ReactiveCityMongoDbRepository;
import net.luversof.boot.autoconfigure.data.empty.EmptyDataPackage;
import net.luversof.boot.autoconfigure.data.mongo.city.City;
import net.luversof.boot.autoconfigure.data.mongo.city.ReactiveCityRepository;


public class MongoReactiveRepositoriesAutoConfigurationTests {
	
	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withPropertyValues("net-profile:opdev")
			.withConfiguration(
					AutoConfigurations.of(MongoAutoConfiguration.class,
							MongoDataAutoConfiguration.class,
							MongoReactiveAutoConfiguration.class,
							MongoReactiveDataAutoConfiguration.class,
							org.springframework.boot.autoconfigure.data.mongo.MongoReactiveRepositoriesAutoConfiguration.class,
							PropertyPlaceholderAutoConfiguration.class,
							MongoReactiveRepositoriesAutoConfiguration.class));
	
	@Test
	public void testDefaultRepositoryConfiguration() {
		this.contextRunner.withUserConfiguration(TestConfiguration.class)
				.run((context) -> {
					assertThat(context).hasSingleBean(ReactiveCityRepository.class);
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
	public void testNoRepositoryConfiguration() {
		this.contextRunner.withUserConfiguration(EmptyConfiguration.class)
				.run((context) -> assertThat(context).hasSingleBean(MongoClient.class));
	}
	
	@Test
	public void doesNotTriggerDefaultRepositoryDetectionIfCustomized() {
		this.contextRunner.withUserConfiguration(CustomizedConfiguration.class)
				.run((context) -> assertThat(context)
						.doesNotHaveBean(ReactiveCityMongoDbRepository.class));
	}
	
	@Test
	public void autoConfigurationShouldNotKickInEvenIfManualConfigDidNotCreateAnyRepositories() {
		this.contextRunner.withUserConfiguration(SortOfInvalidCustomConfiguration.class)
				.run((context) -> assertThat(context)
						.doesNotHaveBean(ReactiveCityRepository.class));
	}

	@Test
	public void enablingImperativeRepositoriesDisablesReactiveRepositories() {
		this.contextRunner.withUserConfiguration(TestConfiguration.class)
				.withPropertyValues("spring.data.mongodb.repositories.type=imperative")
				.run((context) -> assertThat(context)
						.doesNotHaveBean(ReactiveCityRepository.class));
	}
	
	@Test
	public void enablingNoRepositoriesDisablesReactiveRepositories() {
		this.contextRunner.withUserConfiguration(TestConfiguration.class)
				.withPropertyValues("spring.data.mongodb.repositories.type=none")
				.run((context) -> assertThat(context)
						.doesNotHaveBean(ReactiveCityRepository.class));
	}
	
	@Configuration
	@TestAutoConfigurationPackage(City.class)
	protected static class TestConfiguration {

	}

	@Configuration
	@TestAutoConfigurationPackage(EmptyDataPackage.class)
	protected static class EmptyConfiguration {

	}
	
	@Configuration
	@TestAutoConfigurationPackage(MongoReactiveRepositoriesAutoConfigurationTests.class)
	@EnableMongoRepositories(basePackageClasses = CityMongoDbRepository.class)
	protected static class CustomizedConfiguration {

	}
	
	@Configuration
	// To not find any repositories
	@EnableReactiveMongoRepositories("foo.bar")
	@TestAutoConfigurationPackage(MongoReactiveRepositoriesAutoConfigurationTests.class)
	protected static class SortOfInvalidCustomConfiguration {

	}
}
