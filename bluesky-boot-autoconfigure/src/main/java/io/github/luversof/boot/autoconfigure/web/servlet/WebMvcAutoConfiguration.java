package io.github.luversof.boot.autoconfigure.web.servlet;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;

@AutoConfiguration(value = "_blueskyBootWebMvcAutoConfiguration")
@ConditionalOnWebApplication(type = Type.SERVLET)
public class WebMvcAutoConfiguration {

	@Bean
	@ConditionalOnClass(name = {"org.apache.catalina.startup.Tomcat", "ch.qos.logback.access.tomcat.LogbackValve"})
	public LogbackTomcatServletWebServerFactoryCustomizer logbackTomcatServletWebServerFactoryCustomizer() {
		return new LogbackTomcatServletWebServerFactoryCustomizer();
	}

}
