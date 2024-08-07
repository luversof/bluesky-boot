package io.github.luversof.boot.web.servlet;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.core.BlueskyProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * localeContextResolveHandler 별 설정 관리
 * Cookie / AcceptHeader 별 설정 분리가 필요할까? 
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "bluesky-boot.web.servlet.locale-context-resolve-handler")
public class LocaleContextResolveHandlerProperties implements BlueskyProperties {
	
	public static final String DEFAULT_BEAN_NAME = "localeContextResolveHandlerProperties";

	/**
	 * resolveLocale 시 쿠키 생성 여부
	 */
	private Boolean resolveLocaleCookieCreate;

}
