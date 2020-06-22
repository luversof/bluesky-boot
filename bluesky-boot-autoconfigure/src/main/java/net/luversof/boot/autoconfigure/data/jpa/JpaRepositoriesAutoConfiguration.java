package net.luversof.boot.autoconfigure.data.jpa;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration(value = "_blueskyBootJpaRepositoriesAutoConfiguration", proxyBeanMethods = false)
@ConditionalOnClass(JpaRepository.class)
@PropertySource(value = "classpath:data/jpa/data-jpa.properties", ignoreResourceNotFound = true)
@PropertySource(value = "classpath:data/jpa/data-jpa-${net-profile}.properties", ignoreResourceNotFound = true)
@EnableJpaAuditing
public class JpaRepositoriesAutoConfiguration {

}
