package io.github.luversof.boot.autoconfigure.web.servlet;

import java.util.List;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import io.github.luversof.boot.autoconfigure.exception.servlet.CoreMvcExceptionHandler;
import io.github.luversof.boot.web.CookieModuleProperties;
import io.github.luversof.boot.web.CookieProperties;
import io.github.luversof.boot.web.DomainModuleProperties;
import io.github.luversof.boot.web.DomainProperties;
import io.github.luversof.boot.web.filter.BlueskyContextHolderFilter;
import io.github.luversof.boot.web.servlet.i18n.BlueskyLocaleContextResolver;
import io.github.luversof.boot.web.servlet.i18n.CookieLocaleResolverHandler;
import io.github.luversof.boot.web.servlet.i18n.LocaleResolverHandler;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for WebMvc support.
 * @author bluesky
 *
 */
@AutoConfiguration(value = "blueskyBootWebMvcAutoConfiguration", before = org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration.class)
@ConditionalOnWebApplication(type = Type.SERVLET)
@EnableConfigurationProperties({ 
	DomainProperties.class,
	DomainModuleProperties.class
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

    @Bean
    MappingJackson2JsonView jsonView() {
        return new MappingJackson2JsonView();
    }
    
    // (s) test
	@Bean
	@Primary
	CookieProperties cookieProperties() {
		return new CookieProperties();
	}
	
	@Bean
	@Primary
	CookieModuleProperties cookieModuleProperties(CookieProperties cookieProperties) {
		return new CookieModuleProperties(cookieProperties);
	}
	
	@Bean
	@ConfigurationProperties("bluesky-boot.web.other-cookie")
	CookieProperties otherCookieProperties() {
		return new CookieProperties();
	}
	
	@Bean
	@ConfigurationProperties("bluesky-boot.web.other-cookie")
	CookieModuleProperties otherCookieModuleProperties(CookieProperties otherCookieProperties) {
		return new CookieModuleProperties(otherCookieProperties);
	}
    
    @Bean
    CookieLocaleResolverHandler cookieLocaleResolverHandler() {
    	return new CookieLocaleResolverHandler(1);
    }
    
	@Bean /* (DispatcherServlet.LOCALE_RESOLVER_BEAN_NAME) */
    LocaleResolver localeResolver(List<LocaleResolverHandler> localeResolverHandlerList) {
    	return new BlueskyLocaleContextResolver(localeResolverHandlerList);
    }
    
//    public static class WebMvcConfiguration implements WebMvcConfigurer {
//    	
//    	
//    }
    
    // (e) test

	@Bean
	@ConditionalOnClass(name = {"org.apache.catalina.startup.Tomcat", "ch.qos.logback.access.tomcat.LogbackValve"})
	LogbackTomcatServletWebServerFactoryCustomizer logbackTomcatServletWebServerFactoryCustomizer() {
		return new LogbackTomcatServletWebServerFactoryCustomizer();
	}

}
