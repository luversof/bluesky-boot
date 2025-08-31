package io.github.luversof.boot.autoconfigure.web;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import io.github.luversof.boot.web.DomainGroupProperties;
import io.github.luversof.boot.web.DomainModuleProperties;
import io.github.luversof.boot.web.DomainProperties;
import io.github.luversof.boot.web.WebGroupProperties;
import io.github.luversof.boot.web.WebModuleProperties;
import io.github.luversof.boot.web.WebProperties;

@AutoConfiguration("blueskyBootWebAutoConfiguration")
@ConditionalOnWebApplication
@EnableConfigurationProperties({ 
	DomainProperties.class,
	DomainModuleProperties.class,
	DomainGroupProperties.class,
	WebProperties.class,
	WebModuleProperties.class,
	WebGroupProperties.class,
})
public class WebAutoConfiguration {

	@Bean
	MappingJackson2JsonView jsonView() {
		return new MappingJackson2JsonView();
	}
	    
}
