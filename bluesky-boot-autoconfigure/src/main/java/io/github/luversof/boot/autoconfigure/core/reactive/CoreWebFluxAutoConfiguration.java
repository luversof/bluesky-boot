package io.github.luversof.boot.autoconfigure.core.reactive;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import io.github.luversof.boot.autoconfigure.core.CoreAutoConfiguration;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for core reactive support.
 * @author bluesky
 *
 */
@AutoConfiguration(value = "blueskyBootCoreWebFluxAutoConfiguration", after = CoreAutoConfiguration.class)
@ConditionalOnClass(WebFluxConfigurer.class)
@ConditionalOnWebApplication(type = Type.REACTIVE)
public class CoreWebFluxAutoConfiguration {

}
