package io.github.luversof.boot.autoconfigure.security.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import io.github.luversof.boot.autoconfigure.security.exception.servlet.SecurityExceptionHandler;
import io.github.luversof.boot.autoconfigure.security.servlet.WebSecurityConfigurerCustomizer;

@AutoConfiguration("_blueskyBootSecurityServletAutoConfiguration")
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnClass({ DefaultAuthenticationEventPublisher.class, HttpSecurity.class })
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityServletAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public SecurityExceptionHandler securityExceptionHandler() {
		return new SecurityExceptionHandler();
	}
	
	@Bean
	public SecurityFilterChain blueskySecurityFilterchain(HttpSecurity http, UserDetailsService userDetailsService,
			PasswordEncoder passwordEncoder,
			@Autowired(required = false) List<WebSecurityConfigurerCustomizer> webSecurityConfigurerCustomizerList)
			throws Exception {
		
		if (webSecurityConfigurerCustomizerList == null) {
			webSecurityConfigurerCustomizerList = new ArrayList<>();
		}

		http.userDetailsService(userDetailsService);
		
		for (var webSecurityConfigurerCustomizer : webSecurityConfigurerCustomizerList) {
			webSecurityConfigurerCustomizer.preConfigure(http);
		}
		var logoutSuccessHandler = new SimpleUrlLogoutSuccessHandler();
		logoutSuccessHandler.setUseReferer(true);
		
		var authenticationSuccessHandler = new SimpleUrlAuthenticationSuccessHandler();
		authenticationSuccessHandler.setUseReferer(true);
		
		http
			.headers().frameOptions().sameOrigin().and()
			.exceptionHandling().authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)).and()
			.logout().logoutSuccessHandler(logoutSuccessHandler).and()
			.formLogin().loginPage("/login").successHandler(authenticationSuccessHandler).and()
			.rememberMe().and()
            .httpBasic().disable()
            ;
		
		for (var webSecurityConfigurerCustomizer : webSecurityConfigurerCustomizerList) {
			webSecurityConfigurerCustomizer.postConfigure(http);
		}
		
		return http.build();
	}

}
