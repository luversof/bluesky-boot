package io.github.luversof.boot.test.context.runner;

import java.util.function.Supplier;

import org.springframework.boot.autoconfigure.logging.ConditionEvaluationReportLoggingListener;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.test.context.assertj.AssertableReactiveWebApplicationContext;
import org.springframework.boot.test.context.runner.AbstractApplicationContextRunner;
import org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebApplicationContext;
import org.springframework.boot.web.reactive.context.ConfigurableReactiveWebApplicationContext;

import io.github.luversof.boot.context.BlueskyApplicationContextInitializer;
import io.github.luversof.boot.env.ProfileEnvironmentPostProcessor;
import io.github.luversof.boot.security.crypto.env.DecryptEnvironmentPostProcessor;

public class BlueskyReactiveWebApplicationContextRunner extends
AbstractApplicationContextRunner<BlueskyReactiveWebApplicationContextRunner, ConfigurableReactiveWebApplicationContext, AssertableReactiveWebApplicationContext> {
	
	public static BlueskyReactiveWebApplicationContextRunner get() {
		return new BlueskyReactiveWebApplicationContextRunner()
				.withInitializer(ConditionEvaluationReportLoggingListener.forLogLevel(LogLevel.INFO))
				.withInitializer(new BlueskyApplicationContextInitializer())
				.withInitializer(applicationContext -> new ProfileEnvironmentPostProcessor().postProcessEnvironment(applicationContext.getEnvironment(), null))
				.withInitializer(applicationContext -> new DecryptEnvironmentPostProcessor().postProcessEnvironment(applicationContext.getEnvironment(), null));
	}

	private BlueskyReactiveWebApplicationContextRunner() {
		this(AnnotationConfigReactiveWebApplicationContext::new);
	}

	private BlueskyReactiveWebApplicationContextRunner(Supplier<ConfigurableReactiveWebApplicationContext> contextFactory) {
		super(BlueskyReactiveWebApplicationContextRunner::new, contextFactory);
	}

	private BlueskyReactiveWebApplicationContextRunner(
			RunnerConfiguration<ConfigurableReactiveWebApplicationContext> configuration) {
		super(configuration, BlueskyReactiveWebApplicationContextRunner::new);
	}
	
}
