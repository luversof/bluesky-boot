package io.github.luversof.boot.autoconfigure.core;

import static io.github.luversof.boot.autoconfigure.AutoConfigurationTestInfo.BASE_PROPERTY;
import static io.github.luversof.boot.autoconfigure.AutoConfigurationTestInfo.CORE_USER_CONFIGURATION;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import io.github.luversof.boot.autoconfigure.core.constant.TestModuleInfo;
import io.github.luversof.boot.context.BlueskyContextHolder;
import io.github.luversof.boot.core.CoreModuleProperties;
import io.github.luversof.boot.core.CoreProperties;

class CoreAutoConfigurationTests {

	private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
			.withPropertyValues(BASE_PROPERTY)
			.withPropertyValues("bluesky-boot.core.modules.test.domain.web=http://localhost")
			.withPropertyValues("bluesky-boot.core.modules.test.module-info=T(io.github.luversof.boot.autoconfigure.core.constant.TestModuleInfo).TEST")
			.withUserConfiguration(CORE_USER_CONFIGURATION)
			;
	
			
	@AfterEach
	void afterEach() {
		BlueskyContextHolder.clearContext();
	}
	
	@Test
	void coreProperties() {
		this.contextRunner.run(context -> {
			CoreModuleProperties coreModuleProperties = context.getBean(CoreModuleProperties.class);
			assertThat(coreModuleProperties).isNotNull();
			assertThat(context).hasSingleBean(CoreProperties.class);
		});
	}
	
	@Test
	void blueskyContextHolder() {
		this.contextRunner.run(context -> {
			var blueskyContext = BlueskyContextHolder.getContext();
			assertThat((BlueskyContextHolder.getCoreModule()).getModuleInfo()).isEqualTo(TestModuleInfo.TEST);
			assertThat(blueskyContext.getModuleName()).isEqualTo("test");
		});
	}
	
	@Test
	void multiModuleBlueskyContextHolder() {
		this.contextRunner.withPropertyValues("bluesky-boot.core.modules.test2.domain.web=http://localhost").run(context -> {
			BlueskyContextHolder.setContext("test");
			var blueskyContext = BlueskyContextHolder.getContext();
			assertThat((BlueskyContextHolder.getCoreModule()).getModuleInfo()).isEqualTo(TestModuleInfo.TEST);
			assertThat(blueskyContext.getModuleName()).isEqualTo("test");
		});
	}
}
