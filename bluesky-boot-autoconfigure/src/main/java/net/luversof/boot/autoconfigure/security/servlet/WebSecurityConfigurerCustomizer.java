package net.luversof.boot.autoconfigure.security.servlet;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public interface WebSecurityConfigurerCustomizer {

	default void preConfigure(HttpSecurity http) throws Exception {
	}

	default void postConfigure(HttpSecurity http) throws Exception {
	}

}
