package io.github.luversof.boot.autoconfigure.core.controller;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.luversof.boot.autoconfigure.core.CoreProperties;
import io.github.luversof.boot.autoconfigure.devcheck.core.annotation.DevCheckController;
import io.github.luversof.boot.autoconfigure.devcheck.core.annotation.DevCheckDescription;
import io.github.luversof.boot.config.BlueskyProperties;
import io.github.luversof.boot.context.BlueskyContext;
import io.github.luversof.boot.context.BlueskyContextHolder;
import io.github.luversof.boot.util.ApplicationContextUtil;
import lombok.AllArgsConstructor;

/**
 * {@link DevCheckController} for Core support.
 * @author bluesky
 *
 */
@AllArgsConstructor
@DevCheckController
@RestController
@RequestMapping(value = "${bluesky-boot.dev-check.path-prefix}/blueskyBoot/core", produces = MediaType.APPLICATION_JSON_VALUE)
public class CoreDevCheckController {
	
	@DevCheckDescription("Spring activeProfiles 조회")
	@GetMapping("/activeProfiles")
	String[] activeProfiles() {
		return ApplicationContextUtil.getApplicationContext().getEnvironment().getActiveProfiles();
	}
	
	@DevCheckDescription("Spring activeProfiles 조회")
	@GetMapping("/property")
	String property(String key) {
		return ApplicationContextUtil.getApplicationContext().getEnvironment().getProperty(key);
	}
	
	@SuppressWarnings("rawtypes")
	@DevCheckDescription("blueskyPropertiesMap 조회")
	@GetMapping("/blueskyPropertiesMap")
	Map<String, BlueskyProperties> blueskyPropertiesMap() {
		return ApplicationContextUtil.getApplicationContext().getBeansOfType(BlueskyProperties.class);
	}
	
	@DevCheckDescription("coreProperties 조회")
	@GetMapping("/coreProperties")
	CoreProperties coreProperties() {
		return ApplicationContextUtil.getApplicationContext().getBean(CoreProperties.class);
	}
	
	@DevCheckDescription("currentLocale 값 확인.")
	@GetMapping("/currentLocale")
	Locale currentLocale() {
		return LocaleContextHolder.getLocale();
	}
	
	@DevCheckDescription("systemDefaultZone 값 확인.")
	@GetMapping("/systemDefaultZone")
	Clock systemDefaultZone() {
		return Clock.systemDefaultZone();
	}
	
	@DevCheckDescription("localeDateTime now 값 확인.")
	@GetMapping("/localDateTimeNow")
	LocalDateTime localDateTimeNow() {
		return LocalDateTime.now();
	}
	
	@DevCheckDescription("zonedDateTime now 값 확인.")
	@GetMapping("/zonedDateTimeNow")
	ZonedDateTime zonedDateTimeNow() {
		return ZonedDateTime.now();
	}
	
	@DevCheckDescription("blueskyContext 확인")
	@GetMapping("/blueskyContext")
	BlueskyContext blueskyContext() {
		return BlueskyContextHolder.getContext();
	}
}