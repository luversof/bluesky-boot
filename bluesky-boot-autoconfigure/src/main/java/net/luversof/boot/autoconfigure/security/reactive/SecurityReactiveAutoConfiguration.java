package net.luversof.boot.autoconfigure.security.reactive;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.web.server.WebFilterChainProxy;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import reactor.core.publisher.Flux;

@Configuration("_blueskySecurityReactiveAutoConfiguration")
@ConditionalOnWebApplication(type = Type.REACTIVE)
@ConditionalOnClass({ Flux.class, EnableWebFluxSecurity.class, WebFilterChainProxy.class, WebFluxConfigurer.class })
public class SecurityReactiveAutoConfiguration {

	
}
