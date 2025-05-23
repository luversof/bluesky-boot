package io.github.luversof.boot.autoconfigure.data.mongo;

import static io.github.luversof.boot.autoconfigure.AutoConfigurationTestInfo.BASE_PROPERTY;
import static io.github.luversof.boot.autoconfigure.AutoConfigurationTestInfo.DATA_MONGO_REACTIVE_CONFIGURATION;
import static io.github.luversof.boot.autoconfigure.AutoConfigurationTestInfo.DATA_MONGO_REACTIVE_USER_CONFIGURATION;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.Stream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

import com.mongodb.reactivestreams.client.MongoClient;

import io.github.luversof.boot.autoconfigure.AutoConfigurationTestInfo;
import io.github.luversof.boot.autoconfigure.TestAutoConfigurationPackage;
import io.github.luversof.boot.autoconfigure.data.alt.mongo.CityMongoDbRepository;
import io.github.luversof.boot.autoconfigure.data.alt.mongo.ReactiveCityMongoDbRepository;
import io.github.luversof.boot.autoconfigure.data.empty.EmptyDataPackage;
import io.github.luversof.boot.autoconfigure.data.mongo.city.City;
import io.github.luversof.boot.autoconfigure.data.mongo.city.ReactiveCityRepository;
import io.github.luversof.boot.test.context.runner.BlueskyApplicationContextRunner;

@Disabled
class MongoReactiveRepositoriesAutoConfigurationTests {
	
	private final BlueskyApplicationContextRunner contextRunner = BlueskyApplicationContextRunner.get()
			.withPropertyValues(BASE_PROPERTY)
			.withPropertyValues("bluesky-boot.mongodb.default-mongo-properties.host=mongo-service", "bluesky-boot.mongodb.default-mongo-properties.port=27017")
			.withConfiguration(AutoConfigurations.of(Stream.of(AutoConfigurationTestInfo.addClassAll(DATA_MONGO_REACTIVE_CONFIGURATION, org.springframework.boot.autoconfigure.data.mongo.MongoReactiveRepositoriesAutoConfiguration.class)).toArray(Class[]::new)))
			.withUserConfiguration(Stream.of(AutoConfigurationTestInfo.addClassAll(DATA_MONGO_REACTIVE_USER_CONFIGURATION, MongoReactiveRepositoriesAutoConfiguration.class)).toArray(Class[]::new));

	@Test
	void testNoRepositoryConfiguration() {
		this.contextRunner.withUserConfiguration(EmptyConfiguration.class)
				.run(context -> assertThat(context).hasSingleBean(MongoClient.class));
	}

	@Test
	void doesNotTriggerDefaultRepositoryDetectionIfCustomized() {
		this.contextRunner.withUserConfiguration(CustomizedConfiguration.class)
				.run(context -> assertThat(context).doesNotHaveBean(ReactiveCityMongoDbRepository.class));
	}

	@Test
	void autoConfigurationShouldNotKickInEvenIfManualConfigDidNotCreateAnyRepositories() {
		this.contextRunner.withUserConfiguration(SortOfInvalidCustomConfiguration.class)
				.run(context -> assertThat(context).doesNotHaveBean(ReactiveCityRepository.class));
	}

	@Test
	void enablingImperativeRepositoriesDisablesReactiveRepositories() {
		this.contextRunner.withUserConfiguration(TestConfiguration.class)
				.withPropertyValues("spring.data.mongodb.repositories.type=imperative")
				.run(context -> assertThat(context).doesNotHaveBean(ReactiveCityRepository.class));
	}

	@Test
	void enablingNoRepositoriesDisablesReactiveRepositories() {
		this.contextRunner.withUserConfiguration(TestConfiguration.class)
				.withPropertyValues("spring.data.mongodb.repositories.type=none")
				.run(context -> assertThat(context).doesNotHaveBean(ReactiveCityRepository.class));
	}

	@Configuration(proxyBeanMethods = false)
	@TestAutoConfigurationPackage(City.class)
	static class TestConfiguration {

	}

	@Configuration(proxyBeanMethods = false)
	@TestAutoConfigurationPackage(EmptyDataPackage.class)
	static class EmptyConfiguration {

	}

	@Configuration(proxyBeanMethods = false)
	@TestAutoConfigurationPackage(MongoReactiveRepositoriesAutoConfigurationTests.class)
	@EnableMongoRepositories(basePackageClasses = CityMongoDbRepository.class)
	static class CustomizedConfiguration {

	}

	@Configuration(proxyBeanMethods = false)
	// To not find any repositories
	@EnableReactiveMongoRepositories("foo.bar")
	@TestAutoConfigurationPackage(MongoReactiveRepositoriesAutoConfigurationTests.class)
	static class SortOfInvalidCustomConfiguration {

	}
}
