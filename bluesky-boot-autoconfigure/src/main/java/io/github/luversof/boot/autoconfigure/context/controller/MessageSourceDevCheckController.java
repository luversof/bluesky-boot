package io.github.luversof.boot.autoconfigure.context.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.luversof.boot.autoconfigure.devcheck.core.annotation.DevCheckController;
import io.github.luversof.boot.autoconfigure.devcheck.core.annotation.DevCheckDescription;
import io.github.luversof.boot.context.support.BlueskyReloadableResourceBundleMessageSource;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@DevCheckController
@RestController
@RequestMapping(value = "${bluesky-boot.dev-check.path-prefix}/blueskyBoot/messageSource", produces = MediaType.APPLICATION_JSON_VALUE)
public class MessageSourceDevCheckController {
	
	private BlueskyReloadableResourceBundleMessageSource messageSource;

	@GetMapping("/messageSource")
	public MessageSource messageSource() {
		return messageSource;
	}
	
	@DevCheckDescription("다국어 메세지 전체 목록 조회")
	@GetMapping("/messageSources")
	public Map<Object, Object> getMessageSources(@RequestParam(required = false) String searchKeyword) {
		Map<Object, Object> map = new LinkedHashMap<>();
		List<Object> keyList = messageSource.getProperties().keySet().stream()
				.filter(key -> searchKeyword == null || searchKeyword.isEmpty() || String.valueOf(key).toLowerCase().contains(searchKeyword.toLowerCase()))
				.sorted().toList();
		keyList.forEach(key -> map.put(key, messageSource.getProperties().get(key)));
		return map;
	}
	
	@DevCheckDescription("다국어 메세지 Locale별 전체 목록 조회")
	@GetMapping("/messageSourcesByLocale")
	public Map<Object, Object> getMessageSourcesByLocale(@RequestParam(required = false) Locale locale, @RequestParam(required = false) String searchKeyword) {
		final Locale targetLocale = (locale == null) ? LocaleContextHolder.getLocale() : locale;
		Map<Object, Object> map = new LinkedHashMap<>();
		List<Object> keyList = messageSource.getProperties().keySet().stream()
				.filter(key -> searchKeyword == null || searchKeyword.isEmpty() || String.valueOf(key).toLowerCase().contains(searchKeyword.toLowerCase()))
				.sorted().toList();
		
		keyList.forEach(key -> map.put(key, messageSource.getMessage(String.valueOf(key), null, targetLocale)));
		return map;
	}

}
