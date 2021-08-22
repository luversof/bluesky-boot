package io.github.luversof.boot.autoconfigure.kafka;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

class KafkaAutoConfigurationTests {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(KafkaAutoConfiguration.class));
	
	@Test
	void test() {
		this.contextRunner.withPropertyValues("spring.kafka.bootstrap-servers=localhost:9092", "spring.kafka.consumer.group-id=myGroup")
				.run((context) -> {
					@SuppressWarnings("unchecked")
					KafkaTemplate<String, String> kafkaTemplate = context.getBean(KafkaTemplate.class);
					assertThat(kafkaTemplate).isNotNull();
					var result = kafkaTemplate.send("topic1", "test");
				});
	}
}
