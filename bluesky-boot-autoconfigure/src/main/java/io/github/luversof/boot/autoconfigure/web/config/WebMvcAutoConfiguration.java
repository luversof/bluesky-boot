package io.github.luversof.boot.autoconfigure.web.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import io.github.luversof.boot.autoconfigure.web.servlet.handler.TestHandlerExceptionResolver;
import jakarta.servlet.Servlet;

@Configuration(value = "_blueskyBootWebMvcAutoConfiguration", proxyBeanMethods = false)
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnClass({ Servlet.class, DispatcherServlet.class, WebMvcConfigurer.class })
public class WebMvcAutoConfiguration {

    @Bean
    TestHandlerExceptionResolver testHandlerExceptionResolver() {
        return new TestHandlerExceptionResolver();
    }
	
	@Configuration
	public static class WebMvcAutoConfigurationAdapter implements WebMvcConfigurer {
		
		@Autowired
		private TestHandlerExceptionResolver testHandlerExceptionResolver;
		
		@Override
		public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
			var exceptionHandlerExceptionResolver = resolvers.stream().filter(ExceptionHandlerExceptionResolver.class::isInstance).findAny().orElse(null);
			var index = resolvers.indexOf(exceptionHandlerExceptionResolver);
			resolvers.add(index, testHandlerExceptionResolver);
			WebMvcConfigurer.super.extendHandlerExceptionResolvers(resolvers);
		}
		
	}

}
