package io.github.luversof.boot.autoconfigure.core;

import static io.github.luversof.boot.autoconfigure.AutoConfigurationTestInfo.BASE_PROPERTY;
import static io.github.luversof.boot.autoconfigure.AutoConfigurationTestInfo.CORE_USER_CONFIGURATION;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import io.github.luversof.boot.autoconfigure.core.constant.TestModuleInfo;
import io.github.luversof.boot.context.BlueskyContextHolder;
import io.github.luversof.boot.core.CoreModuleProperties;
import io.github.luversof.boot.core.CoreProperties;
import io.github.luversof.boot.test.context.runner.BlueskyApplicationContextRunner;

class CoreAutoConfigurationTests {

	private final BlueskyApplicationContextRunner contextRunner = BlueskyApplicationContextRunner.get()
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
		this.contextRunner.run(_ -> {
			var blueskyContext = BlueskyContextHolder.getContext();
			assertThat((BlueskyContextHolder.getCoreProperties()).getModuleInfo()).isEqualTo(TestModuleInfo.TEST);
			assertThat(blueskyContext.getModuleName()).isEqualTo("test");
		});
	}
	
	@Test
	void blueskyContextHolderCorePropertiesTest() {
		this.contextRunner.run(_ -> {
			var coreProperties = BlueskyContextHolder.getProperties(CoreProperties.class);
			assertThat(coreProperties).isNotNull();
		});
	}
	
	@Test
	void multiModuleBlueskyContextHolder() {
		this.contextRunner.withPropertyValues("bluesky-boot.core.modules.test2.domain.web=http://localhost").run(_ -> {
			BlueskyContextHolder.setContext("test");
			var blueskyContext = BlueskyContextHolder.getContext();
			assertThat((BlueskyContextHolder.getCoreProperties()).getModuleInfo()).isEqualTo(TestModuleInfo.TEST);
			assertThat(blueskyContext.getModuleName()).isEqualTo("test");
		});
	}
}
