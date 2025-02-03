package io.github.luversof.cloud.autoconfigure;

import org.springframework.boot.actuate.autoconfigure.endpoint.EndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration.RefreshProperties;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import io.github.luversof.cloud.context.refresh.BlueskyPropertiesRefresher;
import io.github.luversof.cloud.endpoint.BlueskyRefreshEndpoint;

@AutoConfiguration("blueskyCloudRefreshEndpointAutoConfiguration")
@ConditionalOnClass({RefreshScope.class, EndpointAutoConfiguration.class, Health.class})
@ConditionalOnProperty(prefix = "bluesky-boot.cloud.refresh", name = "enabled", havingValue="true", matchIfMissing = true)
public class BlueskyRefreshEndpointAutoConfiguration {

	@Bean
	BlueskyPropertiesRefresher blueskyPropertiesRefresher(ConfigurableApplicationContext context, RefreshScope scope, RefreshProperties properties) {
		return new BlueskyPropertiesRefresher(context, scope, properties);
	}

	@Bean
	@ConditionalOnAvailableEndpoint
	BlueskyRefreshEndpoint blueskyRefreshEndpoint(BlueskyPropertiesRefresher blueskyPropertiesRefresher) {
		return new BlueskyRefreshEndpoint(blueskyPropertiesRefresher);
	}
	
}
