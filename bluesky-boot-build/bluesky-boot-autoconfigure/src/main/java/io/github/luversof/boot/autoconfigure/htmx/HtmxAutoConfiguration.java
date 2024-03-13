package io.github.luversof.boot.autoconfigure.htmx;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import io.github.luversof.boot.htmx.aspect.HtmxResponseHeaderAspect;

@AutoConfiguration("blueskyBootHtmxAutoConfiguration")
@ConditionalOnProperty(prefix = "bluesky-boot.htmx", name = "enabled", havingValue = "true")
public class HtmxAutoConfiguration {

	@Bean
	HtmxResponseHeaderAspect htmxResponseHeaderAspect() {
		return new HtmxResponseHeaderAspect();
	}

}
