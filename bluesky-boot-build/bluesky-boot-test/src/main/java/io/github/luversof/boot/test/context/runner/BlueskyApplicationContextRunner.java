package io.github.luversof.boot.test.context.runner;

import java.util.function.Supplier;

import org.springframework.boot.autoconfigure.logging.ConditionEvaluationReportLoggingListener;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.test.context.assertj.AssertableApplicationContext;
import org.springframework.boot.test.context.runner.AbstractApplicationContextRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import io.github.luversof.boot.context.BlueskyApplicationContextInitializer;
import io.github.luversof.boot.env.ProfileEnvironmentPostProcessor;
import io.github.luversof.boot.security.crypto.env.DecryptEnvironmentPostProcessor;


public class BlueskyApplicationContextRunner extends AbstractApplicationContextRunner<BlueskyApplicationContextRunner, ConfigurableApplicationContext, AssertableApplicationContext> {

	public static BlueskyApplicationContextRunner get() {
		return new BlueskyApplicationContextRunner()
				.withInitializer(ConditionEvaluationReportLoggingListener.forLogLevel(LogLevel.INFO))
				.withInitializer(new BlueskyApplicationContextInitializer())
				.withInitializer(applicationContext -> new ProfileEnvironmentPostProcessor().postProcessEnvironment(applicationContext.getEnvironment(), null))
				.withInitializer(applicationContext -> new DecryptEnvironmentPostProcessor().postProcessEnvironment(applicationContext.getEnvironment(), null));
	}

	private BlueskyApplicationContextRunner() {
		this(AnnotationConfigApplicationContext::new);
	}

	private BlueskyApplicationContextRunner(Supplier<ConfigurableApplicationContext> contextFactory) {
		super(BlueskyApplicationContextRunner::new, contextFactory);
	}

	private BlueskyApplicationContextRunner(RunnerConfiguration<ConfigurableApplicationContext> runnerConfiguration) {
		super(runnerConfiguration, BlueskyApplicationContextRunner::new);
	}

}
