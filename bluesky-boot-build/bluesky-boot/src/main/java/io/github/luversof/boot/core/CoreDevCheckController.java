package io.github.luversof.boot.core;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;

import io.github.luversof.boot.context.ApplicationContextUtil;
import io.github.luversof.boot.context.BlueskyContext;
import io.github.luversof.boot.context.BlueskyContextHolder;
import io.github.luversof.boot.devcheck.annotation.DevCheckController;
import io.github.luversof.boot.devcheck.annotation.DevCheckDescription;
import lombok.AllArgsConstructor;

/**
 * {@link DevCheckController} for Core support.
 * @author bluesky
 *
 */
@AllArgsConstructor
@DevCheckController
public class CoreDevCheckController {
	
	private static final String PATH_PREFIX = "/blueskyBoot/core";	// NOSONAR java:S1075
	
	@DevCheckDescription("Spring activeProfiles 조회")
	@GetMapping(PATH_PREFIX + "/activeProfiles")
	String[] activeProfiles() {
		return ApplicationContextUtil.getApplicationContext().getEnvironment().getActiveProfiles();
	}
	
	@DevCheckDescription("Spring activeProfiles 조회")
	@GetMapping(PATH_PREFIX + "/property")
	String property(String key) {
		return ApplicationContextUtil.getApplicationContext().getEnvironment().getProperty(key);
	}
	
	@DevCheckDescription("blueskyPropertiesMap 조회")
	@GetMapping(PATH_PREFIX + "/blueskyPropertiesMap")
	Map<String, BlueskyProperties> blueskyPropertiesMap() {
		return ApplicationContextUtil.getApplicationContext().getBeansOfType(BlueskyProperties.class);
	}
	
	@SuppressWarnings("rawtypes")
	@DevCheckDescription("blueskyModulePropertiesMap 조회")
	@GetMapping(PATH_PREFIX + "/blueskyModulePropertiesMap")
	Map<String, BlueskyModuleProperties> blueskyModulePropertiesMap() {
		return ApplicationContextUtil.getApplicationContext().getBeansOfType(BlueskyModuleProperties.class);
	}
	
	@DevCheckDescription("coreProperties 조회")
	@GetMapping(PATH_PREFIX + "/coreProperties")
	CoreProperties coreProperties() {
		return ApplicationContextUtil.getApplicationContext().getBean(CoreProperties.class);
	}
	
	@DevCheckDescription("coreBaseProperties 조회")
	@GetMapping(PATH_PREFIX + "/coreBaseProperties")
	CoreBaseProperties coreBaseProperties() {
		return ApplicationContextUtil.getApplicationContext().getBean(CoreBaseProperties.class);
	}
	
	@DevCheckDescription("coreModuleProperties 조회")
	@GetMapping(PATH_PREFIX + "/coreModuleProperties")
	CoreModuleProperties coreModuleProperties() {
		return ApplicationContextUtil.getApplicationContext().getBean(CoreModuleProperties.class);
	}
	
	@DevCheckDescription("currentLocale 값 확인.")
	@GetMapping(PATH_PREFIX + "/currentLocale")
	Locale currentLocale() {
		return LocaleContextHolder.getLocale();
	}
	
	@DevCheckDescription("systemDefaultZone 값 확인.")
	@GetMapping(PATH_PREFIX + "/systemDefaultZone")
	Clock systemDefaultZone() {
		return Clock.systemDefaultZone();
	}
	
	@DevCheckDescription("localeDateTime now 값 확인.")
	@GetMapping(PATH_PREFIX + "/localDateTimeNow")
	LocalDateTime localDateTimeNow() {
		return LocalDateTime.now();
	}
	
	@DevCheckDescription("zonedDateTime now 값 확인.")
	@GetMapping(PATH_PREFIX + "/zonedDateTimeNow")
	ZonedDateTime zonedDateTimeNow() {
		return ZonedDateTime.now();
	}
	
	@DevCheckDescription("blueskyContext 확인")
	@GetMapping(PATH_PREFIX + "/blueskyContext")
	BlueskyContext blueskyContext() {
		return BlueskyContextHolder.getContext();
	}
}