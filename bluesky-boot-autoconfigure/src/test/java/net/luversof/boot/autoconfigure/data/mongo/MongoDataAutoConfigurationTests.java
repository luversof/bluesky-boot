package net.luversof.boot.autoconfigure.data.mongo;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Set;

import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mapping.model.CamelCaseAbbreviatingFieldNamingStrategy;
import org.springframework.data.mapping.model.FieldNamingStrategy;
import org.springframework.data.mapping.model.PropertyNameFieldNamingStrategy;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.BasicMongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;

import net.luversof.boot.autoconfigure.data.mongo.city.City;
import net.luversof.boot.autoconfigure.data.mongo.country.Country;

public class MongoDataAutoConfigurationTests {
	
	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withPropertyValues("net-profile:opdev")
			.withConfiguration(AutoConfigurations.of(PropertyPlaceholderAutoConfiguration.class,
					MongoAutoConfiguration.class, org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration.class, MongoReactiveRepositoriesAutoConfiguration.class));

//	@Configuration
//	@EnableMongoRepositories(basePackages = "net.luversof.boot.autoconfigure.data.mongo", mongoTemplateRef = "userMongoTemplate")
//	public class Config {
//		
//	}
	
	@Test
	public void templateExists() {
		this.contextRunner.run((context) -> assertThat(context).hasSingleBean(MongoTemplate.class));
	}
	
	@Test
	public void gridFsTemplateExists() {
		this.contextRunner.withPropertyValues("spring.data.mongodb.gridFsDatabase:grid")
				.run((context) -> assertThat(context)
						.hasSingleBean(GridFsTemplate.class));
	}
	
	@Test
	public void customConversions() {
		this.contextRunner.withUserConfiguration(CustomConversionsConfig.class)
				.run((context) -> {
					MongoTemplate template = context.getBean(MongoTemplate.class);
					assertThat(template.getConverter().getConversionService()
							.canConvert(MongoClient.class, Boolean.class)).isTrue();
				});
	}

	@Test
	public void usesAutoConfigurationPackageToPickUpDocumentTypes() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		String cityPackage = City.class.getPackage().getName();
		AutoConfigurationPackages.register(context, cityPackage);
		context.register(MongoAutoConfiguration.class, MongoDataAutoConfiguration.class);
		try {
			context.refresh();
			assertDomainTypesDiscovered(context.getBean(MongoMappingContext.class),
					City.class);
		}
		finally {
			context.close();
		}
	}
	
	@Test
	public void defaultFieldNamingStrategy() {
		this.contextRunner.run((context) -> {
			MongoMappingContext mappingContext = context
					.getBean(MongoMappingContext.class);
			FieldNamingStrategy fieldNamingStrategy = (FieldNamingStrategy) ReflectionTestUtils
					.getField(mappingContext, "fieldNamingStrategy");
			assertThat(fieldNamingStrategy.getClass())
					.isEqualTo(PropertyNameFieldNamingStrategy.class);
		});
	}
	
	@Test
	public void customFieldNamingStrategy() {
		this.contextRunner
				.withPropertyValues("spring.data.mongodb.field-naming-strategy:"
						+ CamelCaseAbbreviatingFieldNamingStrategy.class.getName())
				.run((context) -> {
					MongoMappingContext mappingContext = context
							.getBean(MongoMappingContext.class);
					FieldNamingStrategy fieldNamingStrategy = (FieldNamingStrategy) ReflectionTestUtils
							.getField(mappingContext, "fieldNamingStrategy");
					assertThat(fieldNamingStrategy.getClass())
							.isEqualTo(CamelCaseAbbreviatingFieldNamingStrategy.class);
				});
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void assertDomainTypesDiscovered(MongoMappingContext mappingContext,
			Class<?>... types) {
		Set<Class> initialEntitySet = (Set<Class>) ReflectionTestUtils
				.getField(mappingContext, "initialEntitySet");
		assertThat(initialEntitySet).containsOnly(types);
	}
	
	@Test
	public void interfaceFieldNamingStrategy() {
		this.contextRunner
				.withPropertyValues("spring.data.mongodb.field-naming-strategy:"
						+ FieldNamingStrategy.class.getName())
				.run((context) -> assertThat(context).getFailure()
						.isInstanceOf(BeanCreationException.class));
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void entityScanShouldSetInitialEntitySet() {
		this.contextRunner.withUserConfiguration(EntityScanConfig.class)
				.run((context) -> {
					MongoMappingContext mappingContext = context
							.getBean(MongoMappingContext.class);
					Set<Class<?>> initialEntitySet = (Set<Class<?>>) ReflectionTestUtils
							.getField(mappingContext, "initialEntitySet");
					assertThat(initialEntitySet).containsOnly(City.class, Country.class);
				});

	}
	
	@Test
	public void registersDefaultSimpleTypesWithMappingContext() {
		this.contextRunner.run((context) -> {
			MongoMappingContext mappingContext = context
					.getBean(MongoMappingContext.class);
			BasicMongoPersistentEntity<?> entity = mappingContext
					.getPersistentEntity(Sample.class);
			MongoPersistentProperty dateProperty = entity.getPersistentProperty("date");
			assertThat(dateProperty.isEntity()).isFalse();
		});

	}
	
	@Test
	public void backsOffIfMongoClientBeanIsNotPresent() {
		ApplicationContextRunner runner = new ApplicationContextRunner()
				.withConfiguration(
						AutoConfigurations.of(MongoDataAutoConfiguration.class));
		runner.run((context) -> assertThat(context)
				.doesNotHaveBean(MongoDataAutoConfiguration.class));
	}

	@Test
	public void createsMongoDbFactoryForPreferredMongoClient() {
		this.contextRunner.run((context) -> {
			MongoDatabaseFactory dbFactory = context.getBean(MongoDatabaseFactory.class);
			assertThat(dbFactory).isInstanceOf(SimpleMongoClientDatabaseFactory.class);
		});
	}

	@Test
	public void createsMongoDbFactoryForFallbackMongoClient() {
		this.contextRunner.withUserConfiguration(FallbackMongoClientConfiguration.class)
				.run((context) -> {
					MongoDatabaseFactory dbFactory = context.getBean(MongoDatabaseFactory.class);
					assertThat(dbFactory).isInstanceOf(SimpleMongoClientDatabaseFactory.class);
				});
	}

	
	@Configuration
	static class CustomConversionsConfig {

		@Bean
		public MongoCustomConversions customConversions() {
			return new MongoCustomConversions(Arrays.asList(new MyConverter()));
		}

	}
	
	@Configuration
	@EntityScan("net.luversof.boot.autoconfigure.data.mongo")
	static class EntityScanConfig {

	}
	
	@Configuration
	static class FallbackMongoClientConfiguration {

		@Bean
		com.mongodb.reactivestreams.client.MongoClient fallbackMongoClient() {
			return MongoClients.create();
		}

	}
	
	private static class MyConverter implements Converter<MongoClient, Boolean> {

		@Override
		public Boolean convert(MongoClient source) {
			return null;
		}

	}
	
	static class Sample {

		LocalDateTime date;

	}
//	
//	@Test
//	public void test() {
//		repository.deleteAll();
//
//		// save a couple of customers
//		repository.save(new User("Alice", "Smith"));
//		repository.save(new User("Bob", "Smith"));
//
//		// fetch all customers
//		System.out.println("Customers found with findAll():");
//		System.out.println("-------------------------------");
//		for (User customer : repository.findAll()) {
//			System.out.println(customer);
//		}
//		System.out.println();
//
//		// fetch an individual customer
//		System.out.println("Customer found with findByFirstName('Alice'):");
//		System.out.println("--------------------------------");
//		System.out.println(repository.findByFirstName("Alice"));
//
//		System.out.println("Customers found with findByLastName('Smith'):");
//		System.out.println("--------------------------------");
//		for (User customer : repository.findByLastName("Smith")) {
//			System.out.println(customer);
//		}
//
//	}
//	
//	@Test
//	public void test2() {
//		this.contextRunner.run((context) -> {
//			CityRepository repository = context.getBean(CityRepository.class);
//			log.debug("result : {}", repository.findAll());
//		});
//	}
}
