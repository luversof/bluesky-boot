package io.github.luversof.cloud.autoconfigure;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.actuate.autoconfigure.endpoint.EndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.cloud.autoconfigure.LifecycleMvcEndpointAutoConfiguration;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration;
import org.springframework.cloud.autoconfigure.RefreshAutoConfiguration.RefreshProperties;
import org.springframework.cloud.context.refresh.ConfigDataContextRefresher;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.cloud.util.ConditionalOnBootstrapDisabled;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import io.github.luversof.boot.context.ApplicationContextUtil;
import io.github.luversof.boot.core.BlueskyRefreshProperties;
import io.github.luversof.boot.core.CoreBaseProperties;
import io.github.luversof.boot.core.CoreModuleProperties;
import io.github.luversof.boot.core.CoreProperties;
import io.github.luversof.cloud.endpoint.RefreshEndpoint;
import io.github.luversof.cloud.endpoint.RefreshResetEndpoint;
import lombok.SneakyThrows;

@AutoConfiguration("blueskyCloudRefreshEndpointAutoConfiguration")
@ConditionalOnClass({ EndpointAutoConfiguration.class, Health.class })
@AutoConfigureAfter(LifecycleMvcEndpointAutoConfiguration.class)
@AutoConfigureBefore(RefreshAutoConfiguration.class)
public class RefreshEndpointAutoConfiguration {
	
	@Bean
	@ConditionalOnBootstrapDisabled
	ConfigDataContextRefresher configDataContextRefresher(ConfigurableApplicationContext context, RefreshScope scope, RefreshProperties properties, Environment environment) {
		return new ConfigDataContextRefresher(context, scope, properties) {

			@Override
			protected void updateEnvironment() {
				super.updateEnvironment();
				reloadProperties(CoreBaseProperties.PREFIX, CoreBaseProperties.class);
		    	reloadProperties(CoreProperties.PREFIX, CoreProperties.class);
		    	reloadProperties(CoreModuleProperties.PREFIX, CoreModuleProperties.class, "base", "parent");
			}
			
			@SneakyThrows
		    private <T extends BlueskyRefreshProperties> void reloadProperties(String prefix, Class<T> clazz, String... ignoreProperties) {
		    	var applicationContext = ApplicationContextUtil.getApplicationContext();
		    	T targetProperties = (T) applicationContext.getBean(clazz);
		    	Binder binder = Binder.get(environment);
		    	binder.bind(prefix, clazz).ifBound(newValue -> BeanUtils.copyProperties(newValue, targetProperties, ignoreProperties));
		    	targetProperties.afterPropertiesSet();
		    }
			
		};
	}

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
