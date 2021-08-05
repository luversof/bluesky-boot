package io.github.luversof.boot.autoconfigure.core.config;

import javax.servlet.Servlet;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import io.github.luversof.boot.autoconfigure.core.exception.servlet.error.CoreServletExceptionHandler;
import io.github.luversof.boot.filter.BlueskyContextHolderFilter;

@Configuration(value = "_blueskyCoreServletAutoConfiguration", proxyBeanMethods = false)
@ConditionalOnClass({ Servlet.class, DispatcherServlet.class })
@ConditionalOnWebApplication(type = Type.SERVLET)
@AutoConfigureAfter(CoreAutoConfiguration.class)
public class CoreServletAutoConfiguration {
	
	@Bean
	public BlueskyContextHolderFilter blueskyContextHolderFilter() {
		return new BlueskyContextHolderFilter();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public CoreServletExceptionHandler coreServletExceptionHandler() {
		return new CoreServletExceptionHandler();
	}
	
	@Bean
	public MappingJackson2JsonView jsonView() {
		return new MappingJackson2JsonView();
	}
}
