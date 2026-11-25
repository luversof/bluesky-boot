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

/**
 * {@link DevCheckController} for Core support.
 *
 * @author bluesky
 */
@DevCheckController
@RequestMapping(value = "/blueskyBoot/core", produces = MediaType.APPLICATION_JSON_VALUE)
public class CoreDevCheckController {

  public CoreDevCheckController() {}

  @DevCheckDescription("활성 프로필 조회")
  @GetMapping("/activeProfiles")
  String[] activeProfiles() {
    return ApplicationContextUtil.getApplicationContext().getEnvironment().getActiveProfiles();
  }

  @DevCheckDescription("property 값 조회")
  @GetMapping("/property")
  String property(String key) {
    return ApplicationContextUtil.getApplicationContext().getEnvironment().getProperty(key);
  }

  @DevCheckDescription("BlueskyProperties 빈 목록 조회")
  @GetMapping("/blueskyPropertiesMap")
  Map<String, BlueskyProperties> blueskyPropertiesMap() {
    return ApplicationContextUtil.getApplicationContext().getBeansOfType(BlueskyProperties.class);
  }

  @SuppressWarnings("rawtypes")
  @DevCheckDescription("BlueskyModuleProperties 빈 목록 조회")
  @GetMapping("/blueskyModulePropertiesMap")
  Map<String, BlueskyModuleProperties> blueskyModulePropertiesMap() {
    return ApplicationContextUtil.getApplicationContext()
        .getBeansOfType(BlueskyModuleProperties.class);
  }

  @SuppressWarnings("rawtypes")
  @DevCheckDescription("BlueskyGroupProperties 빈 목록 조회")
  @GetMapping("/blueskyGroupPropertiesMap")
  Map<String, BlueskyGroupProperties> blueskyGroupPropertiesMap() {
    return ApplicationContextUtil.getApplicationContext()
        .getBeansOfType(BlueskyGroupProperties.class);
  }

  @DevCheckDescription("CoreProperties 조회")
  @GetMapping("/coreProperties")
  CoreProperties coreProperties() {
    return ApplicationContextUtil.getApplicationContext().getBean(CoreProperties.class);
  }

  @DevCheckDescription("CoreBaseProperties 조회")
  @GetMapping("/coreBaseProperties")
  CoreBaseProperties coreBaseProperties() {
    return ApplicationContextUtil.getApplicationContext().getBean(CoreBaseProperties.class);
  }

  @DevCheckDescription("CoreModuleProperties 조회")
  @GetMapping("/coreModuleProperties")
  CoreModuleProperties coreModuleProperties() {
    return ApplicationContextUtil.getApplicationContext().getBean(CoreModuleProperties.class);
  }

  @DevCheckDescription("초기 refresh properties 키 목록 조회")
  @GetMapping("/initialBlueskyRefreshPropertiesMapKeySet")
  Set<String> initialBlueskyRefreshPropertiesMapKeySet() {
    return BlueskyBootContextHolder.getContext().getInitialBlueskyRefreshPropertiesMap().keySet();
  }

  @DevCheckDescription("현재 locale 조회")
  @GetMapping("/currentLocale")
  Locale currentLocale() {
    return LocaleContextHolder.getLocale();
  }

  @DevCheckDescription("시스템 기본 시간대 조회")
  @GetMapping("/systemDefaultZone")
  Clock systemDefaultZone() {
    return Clock.systemDefaultZone();
  }

  @DevCheckDescription("현재 LocalDateTime 조회")
  @GetMapping("/localDateTimeNow")
  LocalDateTime localDateTimeNow() {
    return LocalDateTime.now();
  }

  @DevCheckDescription("현재 ZonedDateTime 조회")
  @GetMapping("/zonedDateTimeNow")
  ZonedDateTime zonedDateTimeNow() {
    return ZonedDateTime.now();
  }

  @DevCheckDescription("BlueskyContext 조회")
  @GetMapping("/blueskyContext")
  BlueskyContext blueskyContext() {
    return BlueskyContextHolder.getContext();
  }

  @DevCheckDescription("BlueskyBootContext 조회")
  @GetMapping("/blueskyBootContext")
  BlueskyBootContext blueskyBootContext() {
    return BlueskyBootContextHolder.getContext();
  }
}
