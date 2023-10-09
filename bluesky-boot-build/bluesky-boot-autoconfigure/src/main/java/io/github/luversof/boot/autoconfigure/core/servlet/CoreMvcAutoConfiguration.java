package io.github.luversof.boot.autoconfigure.core.servlet;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import io.github.luversof.boot.autoconfigure.core.CoreAutoConfiguration;
import io.github.luversof.boot.autoconfigure.exception.servlet.CoreMvcExceptionHandler;
import io.github.luversof.boot.core.CoreDevCheckController;
import io.github.luversof.boot.filter.BlueskyContextHolderFilter;
import jakarta.servlet.Servlet;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for core servlet support.
 * @author bluesky
 *
 */
@AutoConfiguration(value = "blueskyBootCoreMvcAutoConfiguration", after = CoreAutoConfiguration.class)
@ConditionalOnClass({ Servlet.class, DispatcherServlet.class })
@ConditionalOnWebApplication(type = Type.SERVLET)
public class CoreMvcAutoConfiguration {

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
    
//    @ConditionalOnProperty(prefix = "bluesky-boot.dev-check", name = "enabled", havingValue = "true")
    @Bean
    CoreDevCheckController coreDevCheckController() {
    	return new CoreDevCheckController();
    }
}
