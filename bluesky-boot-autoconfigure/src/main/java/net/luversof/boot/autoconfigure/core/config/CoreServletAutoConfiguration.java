package net.luversof.boot.autoconfigure.core.config;

import javax.servlet.Servlet;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

import net.luversof.boot.autoconfigure.core.exception.servlet.error.CoreServletExceptionHandler;
import net.luversof.boot.autoconfigure.core.filter.BlueskyContextHolderFilter;

@Configuration
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
}
