package io.github.luversof.boot.autoconfigure.web.servlet;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import io.github.luversof.boot.autoconfigure.web.servlet.error.CoreMvcExceptionHandler;
import io.github.luversof.boot.context.i18n.LocaleProperties;
import io.github.luversof.boot.web.CookieGroupProperties;
import io.github.luversof.boot.web.CookieModuleProperties;
import io.github.luversof.boot.web.CookieProperties;
import io.github.luversof.boot.web.ExternalCookieGroupProperties;
import io.github.luversof.boot.web.ExternalCookieModuleProperties;
import io.github.luversof.boot.web.ExternalCookieProperties;
import io.github.luversof.boot.web.LocaleContextResolverGroupProperties;
import io.github.luversof.boot.web.LocaleContextResolverModuleProperties;
import io.github.luversof.boot.web.LocaleContextResolverProperties;
import io.github.luversof.boot.web.LocaleResolveHandlerGroupProperties;
import io.github.luversof.boot.web.LocaleResolveHandlerModuleProperties;
import io.github.luversof.boot.web.LocaleResolveHandlerProperties;
import io.github.luversof.boot.web.servlet.filter.BlueskyContextHolderFilter;
import io.github.luversof.boot.web.servlet.i18n.BlueskyLocaleContextResolver;
import io.github.luversof.boot.web.servlet.i18n.handler.AcceptHeaderLocaleResolveHandler;
import io.github.luversof.boot.web.servlet.i18n.handler.CookieLocaleResolveHandler;
import io.github.luversof.boot.web.servlet.support.AddPathPatternModuleNameResolver;
import io.github.luversof.boot.web.servlet.support.DomainAddPathPatternModuleNameResolver;
import io.github.luversof.boot.web.servlet.support.DomainModuleNameResolver;
import io.github.luversof.boot.web.servlet.support.ModuleNameResolver;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for WebMvc support.
 * @author bluesky
 *
 */
@AutoConfiguration(value = "blueskyBootWebMvcAutoConfiguration", before = org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration.class)
@ConditionalOnWebApplication(type = Type.SERVLET)
@EnableConfigurationProperties({
	LocaleContextResolverProperties.class,
	LocaleContextResolverModuleProperties.class,
	LocaleContextResolverGroupProperties.class
})
public class WebMvcAutoConfiguration {
	
	@Bean
	MappingJackson2JsonView jsonView() {
		return new MappingJackson2JsonView();
	}
	
	@Bean
	BlueskyContextHolderFilter blueskyContextHolderFilter() {
		return new BlueskyContextHolderFilter();
	}
	
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(prefix = "bluesky-boot.core", name ="resolve-type", havingValue = "domain", matchIfMissing = true)
	ModuleNameResolver domainModuleNameResolver() {
		return new DomainModuleNameResolver();
	}
	
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(prefix = "bluesky-boot.core", name ="resolve-type", havingValue = "add-path-pattern")
	ModuleNameResolver addPathPatternModuleNameResolver() {
		return new AddPathPatternModuleNameResolver();
	}
	
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(prefix = "bluesky-boot.core", name ="resolve-type", havingValue = "domain-add-path-pattern")
	ModuleNameResolver domainAddPathPatternModuleNameResolver() {
		return new DomainAddPathPatternModuleNameResolver();
	}

	@Bean
	@ConditionalOnMissingBean
	CoreMvcExceptionHandler coreMvcExceptionHandler() {
		return new CoreMvcExceptionHandler();
	}
	
	@Bean(LocaleResolveHandlerProperties.ACCEPT_HEADER_BEAN_NAME)
	@ConfigurationProperties(LocaleResolveHandlerProperties.ACCEPT_HEADER_PREFIX)
	LocaleResolveHandlerProperties acceptHeaderLocaleResolveHandlerProperties() {
		return new LocaleResolveHandlerProperties();
	}
	
	@Bean
	@ConfigurationProperties(LocaleResolveHandlerProperties.ACCEPT_HEADER_PREFIX)
	LocaleResolveHandlerModuleProperties acceptHeaderLocaleResolveHandlerModuleProperties(@Qualifier(LocaleResolveHandlerProperties.ACCEPT_HEADER_BEAN_NAME) LocaleResolveHandlerProperties localeResolveHandlerProperties) {
		return new LocaleResolveHandlerModuleProperties(localeResolveHandlerProperties);
	}
	
	@Bean
	@ConfigurationProperties(LocaleResolveHandlerProperties.ACCEPT_HEADER_PREFIX)
	LocaleResolveHandlerGroupProperties acceptHeaderLocaleResolveHandlerGroupProperties(@Qualifier(LocaleResolveHandlerProperties.ACCEPT_HEADER_BEAN_NAME) LocaleResolveHandlerProperties localeResolveHandlerProperties) {
		return new LocaleResolveHandlerGroupProperties(localeResolveHandlerProperties);
	}
	
	@Bean(AcceptHeaderLocaleResolveHandler.DEFAULT_BEAN_NAME)
	AcceptHeaderLocaleResolveHandler acceptHeaderLocaleResolverHandler() {
		return new AcceptHeaderLocaleResolveHandler(LocaleProperties.DEFAULT_BEAN_NAME, null);
	}

	@EnableConfigurationProperties({
		CookieProperties.class,
		CookieModuleProperties.class,
		CookieGroupProperties.class
	})
	@Configuration(proxyBeanMethods = false)
//	@ConditionalOnProperty(prefix = "bluesky-boot.web.cookie", name = "enabled", havingValue = "true")
	public static class WebMvcCookieConfiguration {
		
		
		@Bean(LocaleResolveHandlerProperties.COOKIE_BEAN_NAME)
		@ConfigurationProperties(LocaleResolveHandlerProperties.COOKIE_PREFIX)
		LocaleResolveHandlerProperties cookieLocaleResolveHandlerProperties() {
			return new LocaleResolveHandlerProperties();
		}
		
		@Bean
		@ConfigurationProperties(LocaleResolveHandlerProperties.COOKIE_PREFIX)
		LocaleResolveHandlerModuleProperties cookieLocaleResolveHandlerModuleProperties(@Qualifier(LocaleResolveHandlerProperties.COOKIE_BEAN_NAME) LocaleResolveHandlerProperties localeContextResolveHandlerProperties) {
			return new LocaleResolveHandlerModuleProperties(localeContextResolveHandlerProperties);
		}
		
		@Bean
		@ConfigurationProperties(LocaleResolveHandlerProperties.COOKIE_PREFIX)
		LocaleResolveHandlerGroupProperties cookieLocaleResolveHandlerGroupProperties(@Qualifier(LocaleResolveHandlerProperties.COOKIE_BEAN_NAME) LocaleResolveHandlerProperties localeContextResolveHandlerProperties) {
			return new LocaleResolveHandlerGroupProperties(localeContextResolveHandlerProperties);
		}
		
		@Bean(CookieLocaleResolveHandler.DEFAULT_BEAN_NAME)
		CookieLocaleResolveHandler cookieLocaleResolveHandler() {
			return new CookieLocaleResolveHandler(
					LocaleProperties.DEFAULT_BEAN_NAME,
					CookieProperties.DEFAULT_BEAN_NAME,
					"cookieLocaleContextResolveHandlerProperties"
					);
		}
	}
	
	@EnableConfigurationProperties({
		ExternalCookieProperties.class,
		ExternalCookieModuleProperties.class,
		ExternalCookieGroupProperties.class
	})
	@Configuration(proxyBeanMethods = false)
//	@ConditionalOnProperty(prefix = "bluesky-boot.web.other-cookie", name = "enabled", havingValue = "true")
	public static class WebMvcExternalCookieConfiguration {
		
		@Bean(LocaleResolveHandlerProperties.EXTERNAL_COOKIE_BEAN_NAME)
		@ConfigurationProperties(LocaleResolveHandlerProperties.EXTERNAL_COOKIE_PREFIX)
		LocaleResolveHandlerProperties externalCookieLocaleResolveHandlerProperties() {
			return new LocaleResolveHandlerProperties();
		}
		
		@Bean
		@ConfigurationProperties(LocaleResolveHandlerProperties.EXTERNAL_COOKIE_PREFIX)
		LocaleResolveHandlerModuleProperties externalCookieLocaleResolveHandlerModuleProperties(@Qualifier(LocaleResolveHandlerProperties.EXTERNAL_COOKIE_BEAN_NAME) LocaleResolveHandlerProperties localeResolveHandlerProperties) {
			return new LocaleResolveHandlerModuleProperties(localeResolveHandlerProperties);
		}
		
		@Bean
		@ConfigurationProperties(LocaleResolveHandlerProperties.EXTERNAL_COOKIE_PREFIX)
		LocaleResolveHandlerGroupProperties externalCookieLocaleResolveHandlerGroupProperties(@Qualifier(LocaleResolveHandlerProperties.EXTERNAL_COOKIE_BEAN_NAME) LocaleResolveHandlerProperties localeResolveHandlerProperties) {
			return new LocaleResolveHandlerGroupProperties(localeResolveHandlerProperties);
		}
		
		@Bean
		CookieLocaleResolveHandler externalCookieLocaleResolveHandler() {
			return new CookieLocaleResolveHandler(
					LocaleProperties.EXTERNAL_LOCALE_BEAN_NAME, 
					CookieProperties.EXTERNAL_COOKIE_BEAN_NAME, 
					LocaleResolveHandlerProperties.EXTERNAL_COOKIE_BEAN_NAME
					);
		}

	}

	
	@Bean(DispatcherServlet.LOCALE_RESOLVER_BEAN_NAME)
	@ConditionalOnMissingBean(name = DispatcherServlet.LOCALE_RESOLVER_BEAN_NAME)
	LocaleResolver localeResolver() {
		return new BlueskyLocaleContextResolver();
	}
	
	@Bean
	@ConditionalOnClass(name = { "org.apache.catalina.startup.Tomcat", "ch.qos.logback.access.tomcat.LogbackValve" })
	LogbackTomcatServletWebServerFactoryCustomizer logbackTomcatServletWebServerFactoryCustomizer() {
		return new LogbackTomcatServletWebServerFactoryCustomizer();
	}

}
