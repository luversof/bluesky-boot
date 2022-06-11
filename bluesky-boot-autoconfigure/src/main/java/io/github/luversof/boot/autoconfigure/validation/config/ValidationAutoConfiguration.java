package io.github.luversof.boot.autoconfigure.validation.config;

import javax.validation.Validator;
import javax.validation.executable.ExecutableValidator;

import org.aspectj.weaver.Advice;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.context.annotation.Bean;

import io.github.luversof.boot.autoconfigure.validation.aspect.BlueskyValidatedAspect;

@AutoConfiguration
@ConditionalOnClass({ ExecutableValidator.class, Advice.class })
@ConditionalOnResource(resources = "classpath:META-INF/services/javax.validation.spi.ValidationProvider")
@ConditionalOnProperty(prefix = "spring.aop", name = "auto", havingValue = "true", matchIfMissing = true)
public class ValidationAutoConfiguration {
	
	@Bean
	public BlueskyValidatedAspect blueskyValidatedAspect(Validator validator) {
		return new BlueskyValidatedAspect(validator);
	}

}
