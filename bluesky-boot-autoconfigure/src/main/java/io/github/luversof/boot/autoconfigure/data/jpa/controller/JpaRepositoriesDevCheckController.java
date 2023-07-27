package io.github.luversof.boot.autoconfigure.data.jpa.controller;

import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.web.bind.annotation.GetMapping;

import io.github.luversof.boot.devcheck.annotation.DevCheckController;
import io.github.luversof.boot.devcheck.annotation.DevCheckDescription;
import lombok.AllArgsConstructor;


/**
 * {@link DevCheckController} for Spring Data's JPA Repositories support.
 * @author bluesky
 *
 */
@AllArgsConstructor
@DevCheckController
public class JpaRepositoriesDevCheckController {
	
	private final String pathPrefix = "/blueskyBoot/data/jpa";
	
	private JpaProperties jpaProperties;

	private HibernateProperties hibernateProperties;
	
	@DevCheckDescription("jpaProperties 조회")
	@GetMapping(pathPrefix + "/jpaProperties")
	JpaProperties jpaProperties() {
		return jpaProperties;
	}
	
	@DevCheckDescription("hibernateProperties 조회")
	@GetMapping(pathPrefix + "/hibernateProperties")
	HibernateProperties hibernateProperties() {
		return hibernateProperties;
	}
}
