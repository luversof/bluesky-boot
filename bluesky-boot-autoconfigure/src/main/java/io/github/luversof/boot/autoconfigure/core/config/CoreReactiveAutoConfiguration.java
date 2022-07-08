package io.github.luversof.boot.autoconfigure.core.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@AutoConfiguration(value = "_blueskyBootCoreReactiveAutoConfiguration", after = CoreAutoConfiguration.class)
@ConditionalOnClass(WebFluxConfigurer.class)
@ConditionalOnWebApplication(type = Type.REACTIVE)
public class CoreReactiveAutoConfiguration {

}
