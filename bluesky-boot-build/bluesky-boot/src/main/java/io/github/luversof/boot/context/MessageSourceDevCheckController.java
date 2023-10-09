package io.github.luversof.boot.context;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.github.luversof.boot.context.support.BlueskyReloadableResourceBundleMessageSource;
import io.github.luversof.boot.devcheck.annotation.DevCheckController;
import io.github.luversof.boot.devcheck.annotation.DevCheckDescription;
import lombok.AllArgsConstructor;

/**
 * {@link DevCheckController} for MessageSource support.
 * @author bluesky
 *
 */
@AllArgsConstructor
@DevCheckController
public class MessageSourceDevCheckController {
	
	private final String pathPrefix = "/blueskyBoot/messageSource";
	
	private BlueskyReloadableResourceBundleMessageSource messageSource;

	@GetMapping(pathPrefix + "/messageSource")
	MessageSource messageSource() {
		return messageSource;
	}
	
	@DevCheckDescription("다국어 메세지 전체 목록 조회")
	@GetMapping(pathPrefix + "/messageSources")
	Map<Object, Object> getMessageSources(@RequestParam(required = false) String searchKeyword) {
		var map = new LinkedHashMap<>();
		var keyList = messageSource.getProperties().keySet().stream()
				.filter(key -> searchKeyword == null || searchKeyword.isEmpty() || String.valueOf(key).toLowerCase().contains(searchKeyword.toLowerCase()))
				.sorted().toList();
		keyList.forEach(key -> map.put(key, messageSource.getProperties().get(key)));
		return map;
	}
	
	@DevCheckDescription("다국어 메세지 Locale별 전체 목록 조회")
	@GetMapping(pathPrefix + "/messageSourcesByLocale")
	Map<Object, Object> getMessageSourcesByLocale(@RequestParam(required = false) Locale locale, @RequestParam(required = false) String searchKeyword) {
		final Locale targetLocale = (locale == null) ? LocaleContextHolder.getLocale() : locale;
		var map = new LinkedHashMap<>();
		var keyList = messageSource.getProperties().keySet().stream()
				.filter(key -> searchKeyword == null || searchKeyword.isEmpty() || String.valueOf(key).toLowerCase().contains(searchKeyword.toLowerCase()))
				.sorted().toList();
		
		keyList.forEach(key -> map.put(key, messageSource.getMessage(String.valueOf(key), null, targetLocale)));
		return map;
	}

}
