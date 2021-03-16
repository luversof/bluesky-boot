package io.github.luversof.boot.autoconfigure.core.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration(value = "_blueskyCoreReactiveAutoConfiguration", proxyBeanMethods = false)
@ConditionalOnClass(WebFluxConfigurer.class)
@ConditionalOnWebApplication(type = Type.REACTIVE)
@AutoConfigureAfter(CoreAutoConfiguration.class)
public class CoreReactiveAutoConfiguration {

}
