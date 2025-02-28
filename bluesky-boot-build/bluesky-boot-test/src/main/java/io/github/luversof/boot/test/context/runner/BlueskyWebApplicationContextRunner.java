package io.github.luversof.boot.test.context.runner;

import java.util.function.Supplier;

import org.springframework.boot.test.context.assertj.AssertableWebApplicationContext;
import org.springframework.boot.test.context.runner.AbstractApplicationContextRunner;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebApplicationContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import io.github.luversof.boot.context.BlueskyApplicationContextInitializer;
import io.github.luversof.boot.env.ProfileEnvironmentPostProcessor;
import io.github.luversof.boot.security.crypto.env.DecryptEnvironmentPostProcessor;

public class BlueskyWebApplicationContextRunner extends AbstractApplicationContextRunner<BlueskyWebApplicationContextRunner, ConfigurableWebApplicationContext, AssertableWebApplicationContext> {
	
	public static BlueskyWebApplicationContextRunner get() {
		return new BlueskyWebApplicationContextRunner()
				.withInitializer(new BlueskyApplicationContextInitializer())
				.withInitializer(applicationContext -> new ProfileEnvironmentPostProcessor().postProcessEnvironment(applicationContext.getEnvironment(), null))
				.withInitializer(applicationContext -> new DecryptEnvironmentPostProcessor().postProcessEnvironment(applicationContext.getEnvironment(), null));
	}
	
	private BlueskyWebApplicationContextRunner() {
		this(withMockServletContext(AnnotationConfigServletWebApplicationContext::new));
	}
	
	private BlueskyWebApplicationContextRunner(Supplier<ConfigurableWebApplicationContext> contextFactory) {
		super(BlueskyWebApplicationContextRunner::new, contextFactory);
	}
	
	private BlueskyWebApplicationContextRunner(RunnerConfiguration<ConfigurableWebApplicationContext> configuration) {
		super(configuration, BlueskyWebApplicationContextRunner::new);
	}

	public static Supplier<ConfigurableWebApplicationContext> withMockServletContext(
			Supplier<ConfigurableWebApplicationContext> contextFactory) {
		return (contextFactory != null) ? () -> {
			ConfigurableWebApplicationContext context = contextFactory.get();
			context.setServletContext(new MockServletContext());
			return context;
		} : null;
	}
	
}
