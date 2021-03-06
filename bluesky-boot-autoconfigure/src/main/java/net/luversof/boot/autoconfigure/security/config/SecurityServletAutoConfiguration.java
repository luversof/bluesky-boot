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
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;

import net.luversof.boot.autoconfigure.security.exception.servlet.SecurityExceptionHandler;
import net.luversof.boot.autoconfigure.security.servlet.WebSecurityConfigurerCustomizer;

@Configuration(value = "_blueskySecurityServletAutoConfiguration", proxyBeanMethods = false)
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnClass({ DefaultAuthenticationEventPublisher.class, WebSecurityConfigurerAdapter.class })
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
	public SecurityExceptionHandler securityExceptionHandler(ContentNegotiatingViewResolver contentNegotiatingViewResolver) {
		return new SecurityExceptionHandler(contentNegotiatingViewResolver);
	}
	
	
	@Configuration
	public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
		
		@Autowired
		private PasswordEncoder passwordEncoder;

		@Autowired
		private UserDetailsService userDetailsService;
		
		@Autowired(required = false)
		private List<WebSecurityConfigurerCustomizer> webSecurityConfigurerCustomizerList = new ArrayList<>();
		
		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
			super.configure(auth);
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			for (var webSecurityConfigurerCustomizer : webSecurityConfigurerCustomizerList) {
				webSecurityConfigurerCustomizer.preConfigure(http);
			}
			var logoutSuccessHandler = new SimpleUrlLogoutSuccessHandler();
			logoutSuccessHandler.setUseReferer(true);
			
			var authenticationSuccessHandler = new SimpleUrlAuthenticationSuccessHandler();
			authenticationSuccessHandler.setUseReferer(true);
			
			http
				.headers().frameOptions().sameOrigin().and()
				.exceptionHandling().accessDeniedPage("/error/accessDenied").and()
				.logout().logoutSuccessHandler(logoutSuccessHandler).and()
				.formLogin().loginPage("/login").successHandler(authenticationSuccessHandler).and()
				.rememberMe().and()
//				.csrf().disable()
	            .httpBasic();
			
			for (var webSecurityConfigurerCustomizer : webSecurityConfigurerCustomizerList) {
				webSecurityConfigurerCustomizer.postConfigure(http);
			}
		}
		
	}
}
