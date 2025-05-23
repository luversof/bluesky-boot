package io.github.luversof.boot.core;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import io.github.luversof.boot.context.ApplicationContextUtil;
import io.github.luversof.boot.context.BlueskyBootContext;
import io.github.luversof.boot.context.BlueskyBootContextHolder;
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
@RequestMapping(value = "/blueskyBoot/core", produces = MediaType.APPLICATION_JSON_VALUE)
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
	
	@DevCheckDescription("blueskyPropertiesMap 조회")
	@GetMapping("/blueskyPropertiesMap")
	Map<String, BlueskyProperties> blueskyPropertiesMap() {
		return ApplicationContextUtil.getApplicationContext().getBeansOfType(BlueskyProperties.class);
	}
	
	@SuppressWarnings("rawtypes")
	@DevCheckDescription("blueskyModulePropertiesMap 조회")
	@GetMapping("/blueskyModulePropertiesMap")
	Map<String, BlueskyModuleProperties> blueskyModulePropertiesMap() {
		return ApplicationContextUtil.getApplicationContext().getBeansOfType(BlueskyModuleProperties.class);
	}
	
	@DevCheckDescription("coreProperties 조회")
	@GetMapping("/coreProperties")
	CoreProperties coreProperties() {
		return ApplicationContextUtil.getApplicationContext().getBean(CoreProperties.class);
	}
	
	@DevCheckDescription("coreBaseProperties 조회")
	@GetMapping("/coreBaseProperties")
	CoreBaseProperties coreBaseProperties() {
		return ApplicationContextUtil.getApplicationContext().getBean(CoreBaseProperties.class);
	}
	
	@DevCheckDescription("coreModuleProperties 조회")
	@GetMapping("/coreModuleProperties")
	CoreModuleProperties coreModuleProperties() {
		return ApplicationContextUtil.getApplicationContext().getBean(CoreModuleProperties.class);
	}
	
	@DevCheckDescription("initialBlueskyResfreshPropertiesMapKeySet 조회")
	@GetMapping("/initialBlueskyResfreshPropertiesMapKeySet")
	Set<String> initialBlueskyResfreshPropertiesMapKeySet() {
		return BlueskyBootContextHolder.getContext().getInitialBlueskyResfreshPropertiesMap().keySet();
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
	
	@DevCheckDescription("blueskyBootContext 확인")
	@GetMapping("/blueskyBootContext")
	BlueskyBootContext blueskyBootContext() {
		return BlueskyBootContextHolder.getContext();
	}
	
}