package io.github.luversof.cloud.autoconfigure;

import org.springframework.boot.actuate.autoconfigure.endpoint.EndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.autoconfigure.LifecycleMvcEndpointAutoConfiguration;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.context.annotation.Bean;

import io.github.luversof.cloud.endpoint.RefreshEndpoint;
import io.github.luversof.cloud.endpoint.RefreshResetEndpoint;

@AutoConfiguration("blueskyCloudRefreshEndpointAutoConfiguration")
@ConditionalOnClass({ EndpointAutoConfiguration.class, Health.class })
@AutoConfigureAfter({ LifecycleMvcEndpointAutoConfiguration.class, RefreshAutoConfiguration.class })
public class RefreshEndpointAutoConfiguration {

	@Bean
	@ConditionalOnBean(ContextRefresher.class)
	@ConditionalOnAvailableEndpoint
	@ConditionalOnMissingBean
	RefreshEndpoint blueskyRefreshEndpoint(ContextRefresher contextRefresher) {
		return new RefreshEndpoint(contextRefresher);
	}
	
	@Bean
	@ConditionalOnBean(ContextRefresher.class)
	@ConditionalOnAvailableEndpoint
	@ConditionalOnMissingBean
	RefreshResetEndpoint blueskyRefreshResetEndpoint() {
		return new RefreshResetEndpoint();
	}

}
