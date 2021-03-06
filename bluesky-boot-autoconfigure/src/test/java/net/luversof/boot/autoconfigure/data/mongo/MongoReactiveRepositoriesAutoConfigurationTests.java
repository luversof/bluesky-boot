package net.luversof.boot.autoconfigure.data.mongo;

import static net.luversof.boot.autoconfigure.AutoConfigurationTestInfo.BASE_PROPERTY;
import static net.luversof.boot.autoconfigure.AutoConfigurationTestInfo.DATA_MONGO_REACTIVE_CONFIGURATION;
import static net.luversof.boot.autoconfigure.AutoConfigurationTestInfo.DATA_MONGO_REACTIVE_USER_CONFIGURATION;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.test.util.ReflectionTestUtils;

import com.mongodb.reactivestreams.client.MongoClient;

import net.luversof.boot.autoconfigure.AutoConfigurationTestInfo;
import net.luversof.boot.autoconfigure.TestAutoConfigurationPackage;
import net.luversof.boot.autoconfigure.data.alt.mongo.CityMongoDbRepository;
import net.luversof.boot.autoconfigure.data.alt.mongo.ReactiveCityMongoDbRepository;
import net.luversof.boot.autoconfigure.data.empty.EmptyDataPackage;
import net.luversof.boot.autoconfigure.data.mongo.city.City;
import net.luversof.boot.autoconfigure.data.mongo.city.ReactiveCityRepository;


class MongoReactiveRepositoriesAutoConfigurationTests {
	
	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withPropertyValues(BASE_PROPERTY)
			.withConfiguration(AutoConfigurations.of(Stream.of(AutoConfigurationTestInfo.addClassAll(DATA_MONGO_REACTIVE_CONFIGURATION, org.springframework.boot.autoconfigure.data.mongo.MongoReactiveRepositoriesAutoConfiguration.class)).toArray(Class[]::new)))
			.withUserConfiguration(Stream.of(AutoConfigurationTestInfo.addClassAll(DATA_MONGO_REACTIVE_USER_CONFIGURATION, MongoReactiveRepositoriesAutoConfiguration.class)).toArray(Class[]::new));

	@Test
	void testDefaultRepositoryConfiguration() {
		;
		
		
		this.contextRunner.withUserConfiguration(TestConfiguration.class).run((context) -> {
			assertThat(context).hasSingleBean(ReactiveCityRepository.class);
			assertThat(context).hasSingleBean(MongoClient.class);
			MongoMappingContext mappingContext = context.getBean(MongoMappingContext.class);
			@SuppressWarnings("unchecked")
			Set<? extends Class<?>> entities = (Set<? extends Class<?>>) ReflectionTestUtils.getField(mappingContext,
					"initialEntitySet");
			assertThat(entities).hasSize(1);
		});
	}

	@Test
	void testNoRepositoryConfiguration() {
		this.contextRunner.withUserConfiguration(EmptyConfiguration.class)
				.run((context) -> assertThat(context).hasSingleBean(MongoClient.class));
	}

	@Test
	void doesNotTriggerDefaultRepositoryDetectionIfCustomized() {
		this.contextRunner.withUserConfiguration(CustomizedConfiguration.class)
				.run((context) -> assertThat(context).doesNotHaveBean(ReactiveCityMongoDbRepository.class));
	}

	@Test
	void autoConfigurationShouldNotKickInEvenIfManualConfigDidNotCreateAnyRepositories() {
		this.contextRunner.withUserConfiguration(SortOfInvalidCustomConfiguration.class)
				.run((context) -> assertThat(context).doesNotHaveBean(ReactiveCityRepository.class));
	}

	@Test
	void enablingImperativeRepositoriesDisablesReactiveRepositories() {
		this.contextRunner.withUserConfiguration(TestConfiguration.class)
				.withPropertyValues("spring.data.mongodb.repositories.type=imperative")
				.run((context) -> assertThat(context).doesNotHaveBean(ReactiveCityRepository.class));
	}

	@Test
	void enablingNoRepositoriesDisablesReactiveRepositories() {
		this.contextRunner.withUserConfiguration(TestConfiguration.class)
				.withPropertyValues("spring.data.mongodb.repositories.type=none")
				.run((context) -> assertThat(context).doesNotHaveBean(ReactiveCityRepository.class));
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
