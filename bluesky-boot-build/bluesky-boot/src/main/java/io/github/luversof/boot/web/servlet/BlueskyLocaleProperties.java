package io.github.luversof.boot.web.servlet;

import java.util.List;
import java.util.Locale;

import lombok.Data;

/**
 * 기본적인 Locale 관련 설정을 관리
 */
@Data
public class BlueskyLocaleProperties {
	
	private Locale defaultLocale;

	private List<Locale> enableLocaleList;

}
