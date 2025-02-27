package io.github.luversof.boot.test.context.runner;

import java.util.function.Supplier;

import org.springframework.boot.test.context.assertj.AssertableReactiveWebApplicationContext;
import org.springframework.boot.test.context.runner.AbstractApplicationContextRunner;
import org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebApplicationContext;
import org.springframework.boot.web.reactive.context.ConfigurableReactiveWebApplicationContext;

import io.github.luversof.boot.context.BlueskyApplicationContextInitializer;
import io.github.luversof.boot.env.ProfileEnvironmentPostProcessor;
import io.github.luversof.boot.security.crypto.env.DecryptEnvironmentPostProcessor;

public class BrickReactiveWebApplicationContextRunner extends
AbstractApplicationContextRunner<BrickReactiveWebApplicationContextRunner, ConfigurableReactiveWebApplicationContext, AssertableReactiveWebApplicationContext> {

	public BrickReactiveWebApplicationContextRunner() {
		this(AnnotationConfigReactiveWebApplicationContext::new);
		init();
	}

	public BrickReactiveWebApplicationContextRunner(Supplier<ConfigurableReactiveWebApplicationContext> contextFactory) {
		super(BrickReactiveWebApplicationContextRunner::new, contextFactory);
	}

	public BrickReactiveWebApplicationContextRunner(Supplier<ConfigurableReactiveWebApplicationContext> contextFactory,
			Class<?>... additionalContextInterfaces) {
		super(BrickReactiveWebApplicationContextRunner::new, contextFactory, additionalContextInterfaces);
	}

	private BrickReactiveWebApplicationContextRunner(
			RunnerConfiguration<ConfigurableReactiveWebApplicationContext> configuration) {
		super(configuration, BrickReactiveWebApplicationContextRunner::new);
	}
	
	void init() {
		this
			.withInitializer(new BlueskyApplicationContextInitializer())
			.withInitializer(applicationContext -> new ProfileEnvironmentPostProcessor().postProcessEnvironment(applicationContext.getEnvironment(), null))
			.withInitializer(applicationContext -> new DecryptEnvironmentPostProcessor().postProcessEnvironment(applicationContext.getEnvironment(), null));
	}
}
