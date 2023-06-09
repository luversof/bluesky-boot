package io.github.luversof.boot.autoconfigure.core.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import io.github.luversof.boot.autoconfigure.core.devcheck.controller.CoreDevCheckController;
import io.github.luversof.boot.autoconfigure.core.exception.servlet.error.CoreServletExceptionHandler;
import io.github.luversof.boot.filter.BlueskyContextHolderFilter;
import jakarta.servlet.Servlet;

@AutoConfiguration(value = "_blueskyBootCoreServletAutoConfiguration", after = CoreAutoConfiguration.class)
@ConditionalOnClass({ Servlet.class, DispatcherServlet.class })
@ConditionalOnWebApplication(type = Type.SERVLET)
public class CoreServletAutoConfiguration {

    @Bean
    BlueskyContextHolderFilter blueskyContextHolderFilter() {
        return new BlueskyContextHolderFilter();
    }

    @Bean
    @ConditionalOnMissingBean
    CoreServletExceptionHandler coreServletExceptionHandler(MessageSourceAccessor messageSourceAccessor) {
        return new CoreServletExceptionHandler(messageSourceAccessor);
    }

    @Bean
    MappingJackson2JsonView jsonView() {
        return new MappingJackson2JsonView();
    }
    
//    @ConditionalOnProperty(prefix = "bluesky-boot.dev-check", name = "enabled", havingValue = "true")
    @Bean
    CoreDevCheckController coreDevCheckController() {
    	return new CoreDevCheckController();
    }
}
