package net.luversof.boot.autoconfigure.data.rest;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.rest.webmvc.config.RepositoryRestMvcConfiguration;

@Configuration("_blueskyBootRepositoryRestMvcAutoConfiguration")
@ConditionalOnClass(RepositoryRestMvcConfiguration.class)
@PropertySource(value = "classpath:data/rest/data-rest.properties", ignoreResourceNotFound = true)
public class RepositoryRestMvcAutoConfiguration {

}
