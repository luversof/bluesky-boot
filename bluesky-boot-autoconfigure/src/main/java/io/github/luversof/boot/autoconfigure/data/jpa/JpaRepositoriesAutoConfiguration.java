package io.github.luversof.boot.autoconfigure.data.jpa;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import io.github.luversof.boot.autoconfigure.data.jpa.controller.JpaRepositoriesDevCheckController;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for Spring Data's JPA Repositories support.
 * @author bluesky
 *
 */
@AutoConfiguration("_blueskyBootJpaRepositoriesAutoConfiguration")
@ConditionalOnClass(JpaRepository.class)
@PropertySource(value = "classpath:data/jpa/data-jpa.properties", ignoreResourceNotFound = true)
@PropertySource(value = "classpath:data/jpa/data-jpa-${bluesky-boot-profile}.properties", ignoreResourceNotFound = true)
@EnableJpaAuditing
public class JpaRepositoriesAutoConfiguration {

	@Bean
	JpaRepositoriesDevCheckController jpaRepositoriesDevCheckController(JpaProperties jpaProperties, HibernateProperties hibernateProperties) {
		return new JpaRepositoriesDevCheckController(jpaProperties, hibernateProperties);
	}
}
