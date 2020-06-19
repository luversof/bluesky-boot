package net.luversof.boot.autoconfigure.core;

import static net.luversof.boot.autoconfigure.AutoConfigurationTestInfo.BASE_PROPERTY;
import static net.luversof.boot.autoconfigure.AutoConfigurationTestInfo.CORE_USER_CONFIGURATION;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import net.luversof.boot.autoconfigure.core.config.CoreProperties;
import net.luversof.boot.autoconfigure.core.context.BlueskyContextHolder;

public class CoreAutoConfigurationTests {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withPropertyValues(BASE_PROPERTY)
			.withPropertyValues("bluesky-modules.core.modules.test.domain.web=http://localhost")
			.withUserConfiguration(CORE_USER_CONFIGURATION)
			;
	
			
	@AfterEach
	public void afterEach() {
		BlueskyContextHolder.clearContext();
	}
	
	@Test
	public void coreProperties() {
		this.contextRunner.run(context -> {
			assertThat(context).hasSingleBean(CoreProperties.class);
		});
	}
	
	@Test
	public void blueskyContextHolder() {
		this.contextRunner.run(context -> {
			var blueskyContext = BlueskyContextHolder.getContext();
			assertThat(blueskyContext.getModuleName()).isEqualTo("test");
		});
	}
	
	@Test
	public void multiModuleBlueskyContextHolder() {
		this.contextRunner.withPropertyValues("bluesky-modules.core.modules.test2.domain.web=http://localhost").run(context -> {
			BlueskyContextHolder.setContext("test");
			var blueskyContext = BlueskyContextHolder.getContext();
			assertThat(blueskyContext.getModuleName()).isEqualTo("test");
		});
	}
}
