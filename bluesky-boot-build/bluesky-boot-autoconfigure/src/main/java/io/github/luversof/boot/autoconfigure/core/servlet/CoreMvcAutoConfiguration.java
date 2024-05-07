package io.github.luversof.boot.autoconfigure.core.servlet;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.DispatcherServlet;

import io.github.luversof.boot.autoconfigure.core.CoreAutoConfiguration;
import io.github.luversof.boot.core.CoreDevCheckController;
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

//    @ConditionalOnProperty(prefix = "bluesky-boot.dev-check", name = "enabled", havingValue = "true")
    @Bean
    CoreDevCheckController coreDevCheckController() {
    	return new CoreDevCheckController();
    }
}
