package io.github.luversof.boot.autoconfigure.web.servlet;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import io.github.luversof.boot.web.CookieModuleProperties;
import io.github.luversof.boot.web.CookieProperties;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for WebMvc support.
 * @author bluesky
 *
 */
@AutoConfiguration("blueskyBootWebMvcAutoConfiguration")
@ConditionalOnWebApplication(type = Type.SERVLET)
@EnableConfigurationProperties({ 
	CookieProperties.class,
	CookieModuleProperties.class
})
public class WebMvcAutoConfiguration {

	@Bean
	@ConditionalOnClass(name = {"org.apache.catalina.startup.Tomcat", "ch.qos.logback.access.tomcat.LogbackValve"})
	LogbackTomcatServletWebServerFactoryCustomizer logbackTomcatServletWebServerFactoryCustomizer() {
		return new LogbackTomcatServletWebServerFactoryCustomizer();
	}

}
