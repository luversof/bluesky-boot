package io.github.luversof.boot.autoconfigure.data.mongo;

import static io.github.luversof.boot.autoconfigure.AutoConfigurationTestInfo.BASE_PROPERTY;
import static io.github.luversof.boot.autoconfigure.AutoConfigurationTestInfo.DATA_MONGO_REACTIVE_CONFIGURATION;
import static io.github.luversof.boot.autoconfigure.AutoConfigurationTestInfo.DATA_MONGO_REACTIVE_USER_CONFIGURATION;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataAutoConfiguration;
import org.springframework.boot.test.context.assertj.AssertableApplicationContext;
import org.springframework.data.mongodb.ReactiveMongoDatabaseFactory;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.gridfs.ReactiveGridFsTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import io.github.luversof.boot.test.context.runner.BlueskyApplicationContextRunner;

@Disabled
class MongoReactiveDataAutoConfigurationTests {

	private final BlueskyApplicationContextRunner contextRunner = BlueskyApplicationContextRunner.get()
			.withPropertyValues(BASE_PROPERTY)
			.withPropertyValues("bluesky-boot.mongodb.default-mongo-properties.host=mongo-service", "bluesky-boot.mongodb.default-mongo-properties.port=27017")
			.withConfiguration(AutoConfigurations.of(DATA_MONGO_REACTIVE_CONFIGURATION))
			.withUserConfiguration(DATA_MONGO_REACTIVE_USER_CONFIGURATION);

	@Test
	void templateExists() {
		this.contextRunner.run(context -> assertThat(context).hasSingleBean(ReactiveMongoTemplate.class));
	}

	@Test
	void whenNoGridFsDatabaseIsConfiguredTheGridFsTemplateUsesTheMainDatabase() {
		this.contextRunner.run(context -> assertThat(grisFsTemplateDatabaseName(context)).isEqualTo("test"));
	}

	@Test
	void whenGridFsDatabaseIsConfiguredThenGridFsTemplateUsesIt() {
		this.contextRunner.withPropertyValues("spring.data.mongodb.gridfs.database:grid")
				.run(context -> assertThat(grisFsTemplateDatabaseName(context)).isEqualTo("grid"));
	}

	@Test
	void whenGridFsBucketIsConfiguredThenGridFsTemplateUsesIt() {
		this.contextRunner.withPropertyValues("spring.data.mongodb.gridfs.bucket:test-bucket").run(context -> {
			assertThat(context).hasSingleBean(ReactiveGridFsTemplate.class);
			ReactiveGridFsTemplate template = context.getBean(ReactiveGridFsTemplate.class);
			assertThat(template).hasFieldOrPropertyWithValue("bucket", "test-bucket");
		});
	}

	@Test
	void backsOffIfMongoClientBeanIsNotPresent() {
		BlueskyApplicationContextRunner runner = BlueskyApplicationContextRunner.get()
				.withConfiguration(AutoConfigurations.of(PropertyPlaceholderAutoConfiguration.class, MongoReactiveDataAutoConfiguration.class));
		runner.run(context -> assertThat(context).doesNotHaveBean(MongoReactiveDataAutoConfiguration.class));
	}

	private String grisFsTemplateDatabaseName(AssertableApplicationContext context) {
		assertThat(context).hasSingleBean(ReactiveGridFsTemplate.class);
		ReactiveGridFsTemplate template = context.getBean(ReactiveGridFsTemplate.class);
		ReactiveMongoDatabaseFactory factory = (ReactiveMongoDatabaseFactory) ReflectionTestUtils.getField(template,
				"dbFactory");
		return factory.getMongoDatabase().block().getName();
	}

}
