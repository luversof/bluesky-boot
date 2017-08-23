package net.luversof.boot.autoconfigure.data.jpa;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration("_blueskyBootDataJpaAutoConfiguration")
@ConditionalOnClass(JpaRepository.class)
@PropertySource("classpath:data/jpa/data-jpa.properties")
@PropertySource("classpath:data/jpa/data-jpa-${spring.profiles.active}.properties")
@EnableJpaAuditing
public class JpaAutoConfiguration {

}
