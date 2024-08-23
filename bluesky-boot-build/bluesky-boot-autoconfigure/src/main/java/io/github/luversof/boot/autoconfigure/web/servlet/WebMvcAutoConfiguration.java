package io.github.luversof.boot.autoconfigure.web.servlet;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.LocaleResolver;

import io.github.luversof.boot.autoconfigure.web.servlet.error.CoreMvcExceptionHandler;
import io.github.luversof.boot.context.i18n.LocaleProperties;
import io.github.luversof.boot.web.CookieModuleProperties;
import io.github.luversof.boot.web.CookieProperties;
import io.github.luversof.boot.web.LocaleResolveHandlerModuleProperties;
import io.github.luversof.boot.web.LocaleResolveHandlerProperties;
import io.github.luversof.boot.web.LocaleContextResolverModuleProperties;
import io.github.luversof.boot.web.LocaleContextResolverProperties;
import io.github.luversof.boot.web.filter.BlueskyContextHolderFilter;
import io.github.luversof.boot.web.servlet.BlueskyLocaleContextResolver;
import io.github.luversof.boot.web.servlet.i18n.handler.AcceptHeaderLocaleResolveHandler;
import io.github.luversof.boot.web.servlet.i18n.handler.CookieLocaleResolveHandler;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for WebMvc support.
 * @author bluesky
 *
 */
@AutoConfiguration(value = "blueskyBootWebMvcAutoConfiguration", before = org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration.class)
@ConditionalOnWebApplication(type = Type.SERVLET)
@EnableConfigurationProperties({ 
	LocaleContextResolverProperties.class,
	LocaleContextResolverModuleProperties.class
})
public class WebMvcAutoConfiguration {
	
    @Bean
    BlueskyContextHolderFilter blueskyContextHolderFilter() {
        return new BlueskyContextHolderFilter();
    }

    @Bean
    @ConditionalOnMissingBean
    CoreMvcExceptionHandler coreMvcExceptionHandler() {
        return new CoreMvcExceptionHandler();
    }
    
    @Bean(LocaleResolveHandlerProperties.ACCEPT_HEADER_BEAN_NAME)
	@ConfigurationProperties("bluesky-boot.web.locale-resolve-handler.accept-header")
	LocaleResolveHandlerProperties acceptHeaderLocaleResolveHandlerProperties() {
		return new LocaleResolveHandlerProperties();
	}
	
	@Bean
	@ConfigurationProperties("bluesky-boot.web.locale-resolve-handler.accept-header")
	LocaleResolveHandlerModuleProperties acceptHeaderLocaleResolveHandlerModuleProperties(@Qualifier(LocaleResolveHandlerProperties.ACCEPT_HEADER_BEAN_NAME) LocaleResolveHandlerProperties localeResolveHandlerProperties) {
		return new LocaleResolveHandlerModuleProperties(localeResolveHandlerProperties);
	}
    
    
    @Bean(AcceptHeaderLocaleResolveHandler.DEFAULT_BEAN_NAME)
    AcceptHeaderLocaleResolveHandler acceptHeaderLocaleResolverHandler() {
    	return new AcceptHeaderLocaleResolveHandler(LocaleProperties.DEFAULT_BEAN_NAME, null);
    }

    @Configuration(proxyBeanMethods = false)
//    @ConditionalOnProperty(prefix = "bluesky-boot.web.cookie", name = "enabled", havingValue = "true")
    public static class WebMvcCookieConfiguration {
    	
    	@Bean(CookieProperties.DEFAULT_BEAN_NAME)
    	@Primary
    	@ConfigurationProperties("bluesky-boot.web.cookie")
    	CookieProperties cookieProperties() {
    		return new CookieProperties();
    	}
    	
    	@Bean(CookieModuleProperties.DEFAULT_BEAN_NAME)
    	@Primary
    	@ConfigurationProperties("bluesky-boot.web.cookie")
    	CookieModuleProperties cookieModuleProperties(@Qualifier(CookieProperties.DEFAULT_BEAN_NAME) CookieProperties cookieProperties) {
    		return new CookieModuleProperties(cookieProperties);
    	}
    	
    	@Bean(LocaleResolveHandlerProperties.COOKIE_BEAN_NAME)
    	@Primary
    	@ConfigurationProperties("bluesky-boot.web.locale-resolve-handler.cookie")
    	LocaleResolveHandlerProperties cookieLocaleResolveHandlerProperties() {
    		return new LocaleResolveHandlerProperties();
    	}
    	
    	@Bean
    	@Primary
    	@ConfigurationProperties("bluesky-boot.web.locale-resolve-handler.cookie")
    	LocaleResolveHandlerModuleProperties localeResolveHandlerModuleProperties(@Qualifier(LocaleResolveHandlerProperties.COOKIE_BEAN_NAME) LocaleResolveHandlerProperties localeContextResolveHandlerProperties) {
    		return new LocaleResolveHandlerModuleProperties(localeContextResolveHandlerProperties);
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
    
    @Configuration(proxyBeanMethods = false)
//    @ConditionalOnProperty(prefix = "bluesky-boot.web.other-cookie", name = "enabled", havingValue = "true")
    public static class WebMvcExternalCookieConfiguration {
    	
    	@Bean(CookieProperties.EXTERNAL_COOKIE_BEAN_NAME)
    	@ConfigurationProperties("bluesky-boot.web.external-cookie")
    	CookieProperties externalCookieProperties() {
    		return new CookieProperties();
    	}
    	
    	@Bean(CookieModuleProperties.EXTERNAL_COOKIE_BEAN_NAME)
    	@ConfigurationProperties("bluesky-boot.web.external-cookie")
		CookieModuleProperties externalCookieModuleProperties(@Qualifier(CookieProperties.EXTERNAL_COOKIE_BEAN_NAME) CookieProperties cookieProperties) {
    		return new CookieModuleProperties(cookieProperties);
    	}
    	
    	@Bean(LocaleResolveHandlerProperties.EXTERNAL_COOKIE_BEAN_NAME)
    	@ConfigurationProperties("bluesky-boot.web.locale-resolve-handler.external-cookie")
    	LocaleResolveHandlerProperties externalCookieLocaleResolveHandlerProperties() {
    		return new LocaleResolveHandlerProperties();
    	}
    	
    	@Bean
    	@ConfigurationProperties("bluesky-boot.web.locale-resolve-handler.external-cookie")
    	LocaleResolveHandlerModuleProperties externalLocaleResolveHandlerModuleProperties(@Qualifier(LocaleResolveHandlerProperties.EXTERNAL_COOKIE_BEAN_NAME) LocaleResolveHandlerProperties localeResolveHandlerProperties) {
    		return new LocaleResolveHandlerModuleProperties(localeResolveHandlerProperties);
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
