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
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.LocaleResolver;

import io.github.luversof.boot.autoconfigure.web.servlet.error.CoreMvcExceptionHandler;
import io.github.luversof.boot.context.i18n.LocaleProperties;
import io.github.luversof.boot.web.CookieModuleProperties;
import io.github.luversof.boot.web.CookieProperties;
import io.github.luversof.boot.web.filter.BlueskyContextHolderFilter;
import io.github.luversof.boot.web.servlet.BlueskyLocaleContextResolver;
import io.github.luversof.boot.web.servlet.LocaleContextResolveHandlerProperties;
import io.github.luversof.boot.web.servlet.LocaleContextResolverModuleProperties;
import io.github.luversof.boot.web.servlet.LocaleContextResolverProperties;
import io.github.luversof.boot.web.servlet.i18n.handler.AcceptHeaderLocaleResolverHandler;
import io.github.luversof.boot.web.servlet.i18n.handler.CookieLocaleResolverHandler;

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

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(prefix = "bluesky-boot.web.cookie", name = "enabled", havingValue = "true")
    public static class CookieConfiguration {
    	
    	@Bean(CookieProperties.DEFAULT_BEAN_NAME)
    	@Primary
    	@ConfigurationProperties("bluesky-boot.web.cookie")
    	CookieProperties cookieProperties() {
    		return new CookieProperties();
    	}
    	
    	@Bean
    	@Primary
    	@ConfigurationProperties("bluesky-boot.web.cookie")
    	CookieModuleProperties cookieModuleProperties(@Qualifier(CookieProperties.DEFAULT_BEAN_NAME) CookieProperties cookieProperties) {
    		return new CookieModuleProperties(cookieProperties);
    	}
    	
    	@Bean(LocaleContextResolveHandlerProperties.DEFAULT_BEAN_NAME)
    	@Primary
    	@ConfigurationProperties("bluesky-boot.web.servlet.locale-context-resolve-handler")
    	LocaleContextResolveHandlerProperties localeContextResolveHandlerProperties () {
    		return new LocaleContextResolveHandlerProperties();
    	}
    	
    	@Bean
    	CookieLocaleResolverHandler cookieLocaleResolverHandler() {
    		return new CookieLocaleResolverHandler(
    				1, 
    				LocaleProperties.DEFAULT_BEAN_NAME, 
    				CookieProperties.DEFAULT_BEAN_NAME,
    				LocaleContextResolveHandlerProperties.DEFAULT_BEAN_NAME
    			);
    	}
    }
    
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(prefix = "bluesky-boot.web.other-cookie", name = "enabled", havingValue = "true")
    public static class OtherCookieConfiguration {
    	
    	@Bean
    	@ConfigurationProperties("bluesky-boot.web.other-cookie")
    	CookieProperties otherCookieProperties() {
    		return new CookieProperties();
    	}
    	
    	@Bean
    	@ConfigurationProperties("bluesky-boot.web.other-cookie")
		CookieModuleProperties otherCookieModuleProperties(@Qualifier("otherCookieProperties") CookieProperties otherCookieProperties) {
    		return new CookieModuleProperties(otherCookieProperties);
    	}
    	
    	@Bean
    	@ConfigurationProperties("bluesky-boot.web.servlet.other-locale-context-resolve-handler")
    	LocaleContextResolveHandlerProperties otherLocaleContextResolveHandlerProperties () {
    		return new LocaleContextResolveHandlerProperties();
    	}
    	
    	@Bean
    	CookieLocaleResolverHandler otherCookieLocaleResolverHandler() {
    		return new CookieLocaleResolverHandler(
    				2, 
    				"otherLocaleProperties", 
    				"otherCookieProperties", 
    				"otherLocaleContextResolveHandlerProperties");
    	}

    }
    
    @Bean(AcceptHeaderLocaleResolverHandler.DEFAULT_BEAN_NAME)
    AcceptHeaderLocaleResolverHandler acceptHeaderLocaleResolverHandler() {
    	return new AcceptHeaderLocaleResolverHandler(100, LocaleProperties.DEFAULT_BEAN_NAME, LocaleContextResolveHandlerProperties.DEFAULT_BEAN_NAME);
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
