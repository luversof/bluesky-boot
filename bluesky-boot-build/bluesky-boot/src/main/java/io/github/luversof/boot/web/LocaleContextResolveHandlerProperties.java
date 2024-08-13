package io.github.luversof.boot.web;

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
@ConfigurationProperties(prefix = "bluesky-boot.web.locale-context-resolve-handler")
public class LocaleContextResolveHandlerProperties implements BlueskyProperties {
	
	/**
	 * 로케일 resolve 시 대표 로케일로 지정할지 여부
	 * 해당 설정이 true가 아니면 대표로 지정하지 않음
	 */
	private Boolean setRepresentative;
	
	
	/**
	 * 대표 로케일 지정 옵션
	 * OVERWRITE : 무조건 덮어씀,
	 * OVERWRITE_IF_RESOLVE_LOCALE_NOT_EXISTS : 이미 존재하고 resolveLocale이 없으면 overwrite,
	 * SKIP_IF_EXISTS : 이미 존재하면 넘어감,
	 * SKIP_IF_RESOLVE_LOCALE_EXISTS : 이미 존재하고 RESOLVE_LOCALE이 있으면 넘어감
	 */
	private SetRepresentativeCondition setRepresentativeCondition;
	
	/**
	 * resolveLocaleContext 시 쿠키 생성 여부
	 * 유저가 임의로 언어 변경을 선택한 경우는 무조건 쿠키가 생성되지만
	 * 그외의 경우엔 기본적으로 생성하지 않음
	 */
	private Boolean resolveLocaleContextCookieCreate;
	
	
	/**
	 * 유저가 언어 변경을 선택한 경우 쿠키 생성 여부
	 * 
	 */
	private Boolean setLocaleContextCookieCreate;

	
	public enum SetRepresentativeCondition {
		OVERWRITE,	// 무조건 덮어씀
		OVERWRITE_IF_RESOLVE_LOCALE_NOT_EXISTS,	// 이미 존재하고 resolveLocale이 없으면 overwrite
		SKIP_IF_EXISTS,	// 이미 존재하면 넘어감
		SKIP_IF_RESOLVE_LOCALE_EXISTS	// 이미 존재하고 RESOLVE_LOCALE이 있으면 넘어감
	}

}
