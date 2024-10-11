package io.github.luversof.boot.autoconfigure.data.mongo;

import static io.github.luversof.boot.autoconfigure.AutoConfigurationTestInfo.BASE_PROPERTY;
import static io.github.luversof.boot.autoconfigure.AutoConfigurationTestInfo.DATA_MONGO_CONFIGURATION;
import static io.github.luversof.boot.autoconfigure.AutoConfigurationTestInfo.DATA_MONGO_USER_CONFIGURATION;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mapping.model.CamelCaseAbbreviatingFieldNamingStrategy;
import org.springframework.data.mapping.model.FieldNamingStrategy;
import org.springframework.data.mapping.model.PropertyNameFieldNamingStrategy;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;

@Disabled
class MongoDataAutoConfigurationTests {
	
	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withPropertyValues(BASE_PROPERTY)
			.withPropertyValues("bluesky-boot.mongodb.default-mongo-properties.host=mongo-service", "bluesky-boot.mongodb.default-mongo-properties.port=27017")
			.withConfiguration(AutoConfigurations.of(DATA_MONGO_CONFIGURATION))
			.withUserConfiguration(DATA_MONGO_USER_CONFIGURATION);

	@Test
	void templateExists() {
		this.contextRunner.run(context -> assertThat(context).hasSingleBean(MongoTemplate.class));
	}
	
	@Test
	void gridFsTemplateExists() {
		this.contextRunner.withPropertyValues("spring.data.mongodb.gridFsDatabase:grid")
				.run(context -> assertThat(context)
						.hasSingleBean(GridFsTemplate.class));
	}
	
	@Test
	void customConversions() {
		this.contextRunner.withUserConfiguration(CustomConversionsConfig.class)
				.run(context -> {
					MongoTemplate template = context.getBean(MongoTemplate.class);
					assertThat(template.getConverter().getConversionService()
							.canConvert(MongoClient.class, Boolean.class)).isTrue();
				});
	}

	@Test
	void defaultFieldNamingStrategy() {
		this.contextRunner.run(context -> {
			MongoMappingContext mappingContext = context
					.getBean(MongoMappingContext.class);
			FieldNamingStrategy fieldNamingStrategy = (FieldNamingStrategy) ReflectionTestUtils
					.getField(mappingContext, "fieldNamingStrategy");
			assertThat(fieldNamingStrategy.getClass())
					.isEqualTo(PropertyNameFieldNamingStrategy.class);
		});
	}
	
	@Test
	void customFieldNamingStrategy() {
		this.contextRunner
				.withPropertyValues("spring.data.mongodb.field-naming-strategy:"
						+ CamelCaseAbbreviatingFieldNamingStrategy.class.getName())
				.run(context -> {
					MongoMappingContext mappingContext = context
							.getBean(MongoMappingContext.class);
					FieldNamingStrategy fieldNamingStrategy = (FieldNamingStrategy) ReflectionTestUtils
							.getField(mappingContext, "fieldNamingStrategy");
					assertThat(fieldNamingStrategy.getClass())
							.isEqualTo(CamelCaseAbbreviatingFieldNamingStrategy.class);
				});
	}
	
	@Test
	void interfaceFieldNamingStrategy() {
		this.contextRunner
				.withPropertyValues("spring.data.mongodb.field-naming-strategy:"
						+ FieldNamingStrategy.class.getName())
				.run(context -> assertThat(context).getFailure()
						.isInstanceOf(BeanCreationException.class));
	}
	
	@Test
	void registersDefaultSimpleTypesWithMappingContext() {
		this.contextRunner.run(context -> {
			MongoMappingContext mappingContext = context.getBean(MongoMappingContext.class);
			MongoPersistentEntity<?> entity = mappingContext.getPersistentEntity(Sample.class);
			MongoPersistentProperty dateProperty = entity.getPersistentProperty("date");
			assertThat(dateProperty.isEntity()).isFalse();
		});

	}
	
	@Test
	void createsMongoDbFactoryForPreferredMongoClient() {
		this.contextRunner.run(context -> {
			MongoDatabaseFactory dbFactory = context.getBean(MongoDatabaseFactory.class);
			assertThat(dbFactory).isInstanceOf(SimpleMongoClientDatabaseFactory.class);
		});
	}

	@Test
	void createsMongoDbFactoryForFallbackMongoClient() {
		this.contextRunner.withUserConfiguration(FallbackMongoClientConfiguration.class)
				.run(context -> {
					MongoDatabaseFactory dbFactory = context.getBean(MongoDatabaseFactory.class);
					assertThat(dbFactory).isInstanceOf(SimpleMongoClientDatabaseFactory.class);
				});
	}

	
	@Configuration
	static class CustomConversionsConfig {

        @Bean
        MongoCustomConversions customConversions() {
            return new MongoCustomConversions(Arrays.asList(new MyConverter()));
        }

	}
	
	@Configuration
	@EntityScan("io.github.luversof.boot.autoconfigure.data.mongo")
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
}
