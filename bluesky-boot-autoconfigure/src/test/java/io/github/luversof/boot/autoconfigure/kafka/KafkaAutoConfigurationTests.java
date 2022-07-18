package io.github.luversof.boot.autoconfigure.kafka;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.kafka.core.KafkaTemplate;

class KafkaAutoConfigurationTests {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withConfiguration(AutoConfigurations.of(KafkaAutoConfiguration.class));
	
	@Test
	@Disabled("kafka 서버 제거로 인해 테스트 비활성화")
	void test() {
		this.contextRunner.withPropertyValues("spring.kafka.bootstrap-servers=localhost:9092", "spring.kafka.consumer.group-id=myGroup")
				.run((context) -> {
					@SuppressWarnings("unchecked")
					KafkaTemplate<String, String> kafkaTemplate = context.getBean(KafkaTemplate.class);
					assertThat(kafkaTemplate).isNotNull();
					var result = kafkaTemplate.send("topic1", "test");
					assertThat(result).isNotNull();
				});
	}
}
