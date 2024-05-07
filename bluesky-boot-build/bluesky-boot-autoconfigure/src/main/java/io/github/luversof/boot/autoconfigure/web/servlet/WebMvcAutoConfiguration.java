package io.github.luversof.boot.autoconfigure.web.servlet;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import io.github.luversof.boot.autoconfigure.exception.servlet.CoreMvcExceptionHandler;
import io.github.luversof.boot.web.CookieModuleProperties;
import io.github.luversof.boot.web.CookieProperties;
import io.github.luversof.boot.web.DomainModuleProperties;
import io.github.luversof.boot.web.DomainProperties;
import io.github.luversof.boot.web.filter.BlueskyContextHolderFilter;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for WebMvc support.
 * @author bluesky
 *
 */
@AutoConfiguration("blueskyBootWebMvcAutoConfiguration")
@ConditionalOnWebApplication(type = Type.SERVLET)
@EnableConfigurationProperties({ 
	CookieProperties.class,
	CookieModuleProperties.class,
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

	@Bean
	@ConditionalOnClass(name = {"org.apache.catalina.startup.Tomcat", "ch.qos.logback.access.tomcat.LogbackValve"})
	LogbackTomcatServletWebServerFactoryCustomizer logbackTomcatServletWebServerFactoryCustomizer() {
		return new LogbackTomcatServletWebServerFactoryCustomizer();
	}

}
