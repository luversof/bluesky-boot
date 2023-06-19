package io.github.luversof.boot.autoconfigure.data.jpa.controller;

import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.luversof.boot.autoconfigure.devcheck.core.annotation.DevCheckController;
import io.github.luversof.boot.autoconfigure.devcheck.core.annotation.DevCheckDescription;
import lombok.AllArgsConstructor;


/**
 * {@link DevCheckController} for Spring Data's JPA Repositories support.
 * @author bluesky
 *
 */
@AllArgsConstructor
@DevCheckController
@RestController
@RequestMapping(value = "${bluesky-boot.dev-check.path-prefix}/blueskyBoot/data/jpa", produces = MediaType.APPLICATION_JSON_VALUE)
public class JpaRepositoriesDevCheckController {
	
	private JpaProperties jpaProperties;

	private HibernateProperties hibernateProperties;
	
	@DevCheckDescription("jpaProperties 조회")
	@GetMapping("/jpaProperties")
	JpaProperties jpaProperties() {
		return jpaProperties;
	}
	
	@DevCheckDescription("hibernateProperties 조회")
	@GetMapping("/hibernateProperties")
	HibernateProperties hibernateProperties() {
		return hibernateProperties;
	}
}
