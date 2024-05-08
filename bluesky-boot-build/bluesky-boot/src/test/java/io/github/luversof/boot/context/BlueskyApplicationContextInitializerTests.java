package io.github.luversof.boot.context;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class BlueskyApplicationContextInitializerTests {

	@Test
	void applicationContextUtilTest() {
		load();
		ApplicationContext applicationContext = ApplicationContextUtil.getApplicationContext();
		assertThat(applicationContext).isNotNull();
	}
	
	
	private void load() {
		try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
			new BlueskyApplicationContextInitializer().initialize(context);
			context.refresh();
		}
	}
	
}
