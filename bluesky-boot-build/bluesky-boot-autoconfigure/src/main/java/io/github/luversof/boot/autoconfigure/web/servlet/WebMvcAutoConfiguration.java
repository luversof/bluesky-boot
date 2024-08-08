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
import io.github.luversof.boot.web.LocaleContextResolveHandlerModuleProperties;
import io.github.luversof.boot.web.LocaleContextResolveHandlerProperties;
import io.github.luversof.boot.web.LocaleContextResolverModuleProperties;
import io.github.luversof.boot.web.LocaleContextResolverProperties;
import io.github.luversof.boot.web.filter.BlueskyContextHolderFilter;
import io.github.luversof.boot.web.servlet.BlueskyLocaleContextResolver;
import io.github.luversof.boot.web.servlet.i18n.handler.AcceptHeaderLocaleContextResolveHandler;
import io.github.luversof.boot.web.servlet.i18n.handler.CookieLocaleContextResolveHandler;

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
    
    
    @Bean(AcceptHeaderLocaleContextResolveHandler.DEFAULT_BEAN_NAME)
    AcceptHeaderLocaleContextResolveHandler acceptHeaderLocaleContextResolverHandler() {
    	return new AcceptHeaderLocaleContextResolveHandler(300, LocaleProperties.DEFAULT_BEAN_NAME, null);
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
    	
    	@Bean
    	@Primary
    	@ConfigurationProperties("bluesky-boot.web.cookie")
    	CookieModuleProperties cookieModuleProperties(@Qualifier(CookieProperties.DEFAULT_BEAN_NAME) CookieProperties cookieProperties) {
    		return new CookieModuleProperties(cookieProperties);
    	}
    	
    	@Bean
    	@Primary
    	@ConfigurationProperties("bluesky-boot.web.locale-context-resolve-handler.cookie")
    	LocaleContextResolveHandlerProperties cookieLocaleContextResolveHandlerProperties() {
    		return new LocaleContextResolveHandlerProperties();
    	}
    	
    	@Bean
    	@Primary
    	@ConfigurationProperties("bluesky-boot.web.locale-context-resolve-handler.cookie")
    	LocaleContextResolveHandlerModuleProperties localeContextResolveHandlerModuleProperties(@Qualifier("cookieLocaleContextResolveHandlerProperties") LocaleContextResolveHandlerProperties localeContextResolveHandlerProperties) {
    		return new LocaleContextResolveHandlerModuleProperties(localeContextResolveHandlerProperties);
    	}
    	
    	@Bean
    	CookieLocaleContextResolveHandler cookieLocaleResolveHandler() {
    		return new CookieLocaleContextResolveHandler(
    				200,
    				LocaleProperties.DEFAULT_BEAN_NAME,
    				CookieProperties.DEFAULT_BEAN_NAME,
    				"cookieLocaleContextResolveHandlerProperties"
    				);
    	}
    }
    
    @Configuration(proxyBeanMethods = false)
//    @ConditionalOnProperty(prefix = "bluesky-boot.web.other-cookie", name = "enabled", havingValue = "true")
    public static class WebMvcOtherCookieConfiguration {
    	
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
    	@ConfigurationProperties("bluesky-boot.web.locale-context-resolve-handler.other-cookie")
    	LocaleContextResolveHandlerProperties otherCookieLocaleContextResolveHandlerProperties() {
    		return new LocaleContextResolveHandlerProperties();
    	}
    	
    	@Bean
    	@ConfigurationProperties("bluesky-boot.web.locale-context-resolve-handler.other-cookie")
    	LocaleContextResolveHandlerModuleProperties otherLocaleContextResolveHandlerModuleProperties(@Qualifier("otherCookieLocaleContextResolveHandlerProperties") LocaleContextResolveHandlerProperties localeContextResolveHandlerProperties) {
    		return new LocaleContextResolveHandlerModuleProperties(localeContextResolveHandlerProperties);
    	}
    	
    	@Bean
    	CookieLocaleContextResolveHandler otherCookieLocaleResolveHandler() {
    		return new CookieLocaleContextResolveHandler(
    				100, 
    				"otherLocaleProperties", 
    				"otherCookieProperties", 
    				"otherLocaleContextResolveHandlerProperties");
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
