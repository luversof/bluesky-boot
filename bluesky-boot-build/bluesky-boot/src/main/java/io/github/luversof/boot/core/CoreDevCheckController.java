package io.github.luversof.boot.core;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;

import io.github.luversof.boot.context.BlueskyContext;
import io.github.luversof.boot.context.BlueskyContextHolder;
import io.github.luversof.boot.devcheck.annotation.DevCheckController;
import io.github.luversof.boot.devcheck.annotation.DevCheckDescription;
import io.github.luversof.boot.util.ApplicationContextUtil;
import lombok.AllArgsConstructor;

/**
 * {@link DevCheckController} for Core support.
 * @author bluesky
 *
 */
@AllArgsConstructor
@DevCheckController
public class CoreDevCheckController {
	
	private final String pathPrefix = "/blueskyBoot/core";
	
	@DevCheckDescription("Spring activeProfiles 조회")
	@GetMapping(pathPrefix + "/activeProfiles")
	String[] activeProfiles() {
		return ApplicationContextUtil.getApplicationContext().getEnvironment().getActiveProfiles();
	}
	
	@DevCheckDescription("Spring activeProfiles 조회")
	@GetMapping(pathPrefix + "/property")
	String property(String key) {
		return ApplicationContextUtil.getApplicationContext().getEnvironment().getProperty(key);
	}
	
	@SuppressWarnings("rawtypes")
	@DevCheckDescription("blueskyPropertiesMap 조회")
	@GetMapping(pathPrefix + "/blueskyPropertiesMap")
	Map<String, BlueskyProperties> blueskyPropertiesMap() {
		return ApplicationContextUtil.getApplicationContext().getBeansOfType(BlueskyProperties.class);
	}
	
	@DevCheckDescription("coreProperties 조회")
	@GetMapping(pathPrefix + "/coreProperties")
	CoreProperties coreProperties() {
		return ApplicationContextUtil.getApplicationContext().getBean(CoreProperties.class);
	}
	
	@DevCheckDescription("currentLocale 값 확인.")
	@GetMapping(pathPrefix + "/currentLocale")
	Locale currentLocale() {
		return LocaleContextHolder.getLocale();
	}
	
	@DevCheckDescription("systemDefaultZone 값 확인.")
	@GetMapping(pathPrefix + "/systemDefaultZone")
	Clock systemDefaultZone() {
		return Clock.systemDefaultZone();
	}
	
	@DevCheckDescription("localeDateTime now 값 확인.")
	@GetMapping(pathPrefix + "/localDateTimeNow")
	LocalDateTime localDateTimeNow() {
		return LocalDateTime.now();
	}
	
	@DevCheckDescription("zonedDateTime now 값 확인.")
	@GetMapping(pathPrefix + "/zonedDateTimeNow")
	ZonedDateTime zonedDateTimeNow() {
		return ZonedDateTime.now();
	}
	
	@DevCheckDescription("blueskyContext 확인")
	@GetMapping(pathPrefix + "/blueskyContext")
	BlueskyContext blueskyContext() {
		return BlueskyContextHolder.getContext();
	}
}