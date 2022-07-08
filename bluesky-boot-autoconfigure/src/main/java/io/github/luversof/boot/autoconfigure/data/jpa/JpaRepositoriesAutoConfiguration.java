package io.github.luversof.boot.autoconfigure.data.jpa;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@AutoConfiguration("_blueskyBootJpaRepositoriesAutoConfiguration")
@ConditionalOnClass(JpaRepository.class)
@PropertySource(value = "classpath:data/jpa/data-jpa.properties", ignoreResourceNotFound = true)
@PropertySource(value = "classpath:data/jpa/data-jpa-${net-profile}.properties", ignoreResourceNotFound = true)
@EnableJpaAuditing
public class JpaRepositoriesAutoConfiguration {

}
