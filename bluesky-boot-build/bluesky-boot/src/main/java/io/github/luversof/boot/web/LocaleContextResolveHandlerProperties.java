package io.github.luversof.boot.web;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.core.BlueskyProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
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
	 * 쿠키 설정 관련 옵션
	 * resolveLocaleContext 시 쿠키 생성 여부
	 * 유저가 임의로 언어 변경을 선택한 경우는 무조건 쿠키가 생성되지만
	 * 그외의 경우엔 기본적으로 생성하지 않음
	 */
	private Boolean resolveLocaleContextCookieCreate;
	
	/**
	 * 쿠키 설정 관련 옵션
	 * 유저가 언어 변경을 선택한 경우 쿠키 생성 여부
	 * 
	 */
	private Boolean setLocaleContextCookieCreate;
	
	/**
	 * 대표 로케일 지정 옵션
	 * 앞서 계산된 대표 로케일에 대해 후속 handler의 지정 여부 옵션임
	 * NONE : 대표 로케일로 지정하지 않음
	 * OVERWRITE : 무조건 지정,
	 * OVERWRITE_WHEN_NOT_EXISTS : 이미 존재하는 대표 로케일의 resolveLocale이 없으면 지정
	 * SKIP_IF_EXISTS : 이미 존재하는 대표 로케일이 있으면 넘어감 (이렇게 쓸일은 없을 듯)
	 */
	private SetRepresentativeCondition setRepresentativeCondition;
	
	/**
	 * 앞서 계산된 handler의 resolveLocaleContext 참조 옵션
	 * NONE : 참조하지 않음
	 * USE_LAST_MATCH_FIRST : 선행 handler의 resovleContext 값을 우선 참조
	 * USE_LAST_MATCH_WHEN_NOT_RESOLVED : resolveLocale이 없는 경우 선행 handler의 resolveContext값을 참조
	 * USE_LAST_MATCH_LANGUAGE_FIRST : 선행 handler의 resolveContext 값의 언어를 기준으로 우선 참조
	 * USE_LAST_MATCH_LANGUAGE_WHEN_NOT_RESOLVED : resolveLocale이 없는 경우 선행 handler의 resolveContext값의 언어를 참조  
	 */
	private UsePreLocaleContextResolveInfoCondition usePreLocaleContextResolveInfoCondition;
	
	
	public enum SetRepresentativeCondition {
		NONE,
		OVERWRITE,
		OVERWRITE_IF_NOT_EXISTS,
	}
	
	@AllArgsConstructor
	@Getter
	public enum UsePreLocaleContextResolveInfoCondition {
		NONE(false, false),
		USE_FIRST(true, false),
		USE_WHEN_NOT_RESOLVED(false,false),
		USE_LANGUAGE_FIRST(true, true),
		USE_LANGUAGE_WHEN_NOT_RESOLVED(false,true),
		;
		
		/**
		 * 선행 handler의 결과를 우선 참조
		 */
		private boolean usePreResolveLocaleFirst;
		
		/**
		 * language 일치 체크의 경우
		 */
		private boolean checkLanguageMatchOnly;
	}
}
