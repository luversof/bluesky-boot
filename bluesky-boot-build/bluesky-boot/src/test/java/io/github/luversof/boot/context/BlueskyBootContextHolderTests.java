package io.github.luversof.boot.context;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.ConfigurationWarningsApplicationContextInitializer;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import io.github.luversof.boot.core.CoreBaseProperties;
import io.github.luversof.boot.core.CoreModuleProperties;
import io.github.luversof.boot.core.CoreProperties;

class BlueskyBootContextHolderTests {

	private AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
	
	@AfterEach
	void cleanUp() {
		this.context.close();
	}
	
	private void load() {
		new ConfigurationWarningsApplicationContextInitializer().initialize(context);
		new BlueskyApplicationContextInitializer().initialize(context);
		context.register(TestConfiguration.class);
		context.refresh();
	}

	@Test
	void getBlueskyContext() {
		load();
		var blueskyContext = BlueskyContextHolder.getContext();
		assertThat(blueskyContext).isNotNull();
	}
	
	@Test
	@Disabled("ContextHolder 테스트는 전체 테스트 수행 시 code coverage 처리가 되지 않음")
	void getBlueskyContext2() {
		System.setProperty(BlueskyContextHolder.SYSTEM_PROPERTY, BlueskyContextHolder.MODE_GLOBAL);
		load();
		var blueskyContext = BlueskyContextHolder.getContext();
		assertThat(blueskyContext).isNotNull();
	}
	
	@Test
	void getBlueskyBootContext() {
		load();
		var blueskyContext = BlueskyBootContextHolder.getContext();
		assertThat(blueskyContext).isNotNull();
	}
	
	@Configuration(proxyBeanMethods = false)
	@EnableConfigurationProperties({
		CoreBaseProperties.class, 
		CoreProperties.class, 
		CoreModuleProperties.class 
	})
	static class TestConfiguration {
		
	}
}
