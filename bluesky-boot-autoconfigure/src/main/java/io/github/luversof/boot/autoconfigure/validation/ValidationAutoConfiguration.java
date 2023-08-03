package io.github.luversof.boot.autoconfigure.validation;

import org.aspectj.weaver.Advice;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.context.annotation.Bean;

import io.github.luversof.boot.validation.aspect.BlueskyValidatedAspect;
import jakarta.validation.Validator;
import jakarta.validation.executable.ExecutableValidator;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Validation support.
 * @author bluesky
 *
 */
@AutoConfiguration
@ConditionalOnClass({ ExecutableValidator.class, Advice.class })
@ConditionalOnResource(resources = "classpath:META-INF/services/javax.validation.spi.ValidationProvider")
@ConditionalOnProperty(prefix = "bluesky-boot.aop", name = "auto", havingValue = "true")
public class ValidationAutoConfiguration {

    @Bean
    BlueskyValidatedAspect blueskyValidatedAspect(Validator validator) {
        return new BlueskyValidatedAspect(validator);
    }

}
