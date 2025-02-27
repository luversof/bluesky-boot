package io.github.luversof.boot.test.context.runner;

import java.util.function.Supplier;

import org.springframework.boot.test.context.assertj.AssertableApplicationContext;
import org.springframework.boot.test.context.runner.AbstractApplicationContextRunner;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import io.github.luversof.boot.context.BlueskyApplicationContextInitializer;
import io.github.luversof.boot.env.ProfileEnvironmentPostProcessor;
import io.github.luversof.boot.security.crypto.env.DecryptEnvironmentPostProcessor;


public class BrickApplicationContextRunner extends AbstractApplicationContextRunner<BrickApplicationContextRunner, ConfigurableApplicationContext, AssertableApplicationContext> {


	public BrickApplicationContextRunner() {
		this(AnnotationConfigApplicationContext::new);
		init();
	}

	public BrickApplicationContextRunner(Supplier<ConfigurableApplicationContext> contextFactory) {
		super(BrickApplicationContextRunner::new, contextFactory);
	}

	/**
	 * Create a new {@link ApplicationContextRunner} instance using the specified
	 * {@code contextFactory} as the underlying source.
	 * @param contextFactory a supplier that returns a new instance on each call
	 * @param additionalContextInterfaces any additional application context interfaces to
	 * be added to the application context proxy
	 * @since 3.4.0
	 */
	public BrickApplicationContextRunner(Supplier<ConfigurableApplicationContext> contextFactory, Class<?>... additionalContextInterfaces) {
		super(BrickApplicationContextRunner::new, contextFactory, additionalContextInterfaces);
	}

	private BrickApplicationContextRunner(RunnerConfiguration<ConfigurableApplicationContext> runnerConfiguration) {
		super(runnerConfiguration, BrickApplicationContextRunner::new);
	}

	void init() {
		this
			.withInitializer(new BlueskyApplicationContextInitializer())
			.withInitializer(applicationContext -> new ProfileEnvironmentPostProcessor().postProcessEnvironment(applicationContext.getEnvironment(), null))
			.withInitializer(applicationContext -> new DecryptEnvironmentPostProcessor().postProcessEnvironment(applicationContext.getEnvironment(), null));
	}
	
}
