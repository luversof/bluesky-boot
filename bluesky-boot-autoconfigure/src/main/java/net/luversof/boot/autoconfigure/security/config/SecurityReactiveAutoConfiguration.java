package net.luversof.boot.autoconfigure.security.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.WebFilterChainProxy;
import org.springframework.security.web.server.header.XFrameOptionsServerHttpHeadersWriter.Mode;
import org.springframework.web.reactive.config.WebFluxConfigurer;

import net.luversof.boot.autoconfigure.security.reactive.SecurityWebFilterChainCustomizer;
import reactor.core.publisher.Flux;

@Configuration("_blueskySecurityReactiveAutoConfiguration")
@ConditionalOnWebApplication(type = Type.REACTIVE)
@ConditionalOnClass({ Flux.class, EnableWebFluxSecurity.class, WebFilterChainProxy.class, WebFluxConfigurer.class })
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityReactiveAutoConfiguration {
	
	@Autowired(required = false)
	private List<SecurityWebFilterChainCustomizer> securityWebFilterChainCustomizerList = new ArrayList<>();

	@Bean
	@ConditionalOnMissingBean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public SecurityWebFilterChain configure(ServerHttpSecurity http) {
		securityWebFilterChainCustomizerList.forEach(customizer -> customizer.preConfigure(http));
		
	    http
	    	.headers().frameOptions().mode(Mode.SAMEORIGIN).and()
	    	.authorizeExchange()
	    		.anyExchange().permitAll()
	    		.and()
//	    	 .exceptionHandling().accessDeniedHandler(accessDeniedHandler) // 이거 어떻게 바뀌었을까?
//	    	.logout().logoutSuccessHandler(logoutSuccessHandler) // 5.0 이후
	    	.formLogin().and()
	    	.csrf().disable()
	    	.httpBasic().and();
	    securityWebFilterChainCustomizerList.forEach(customizer -> customizer.postConfigure(http));
	    
	    return http.build();
	}
}
