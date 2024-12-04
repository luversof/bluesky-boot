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
@ConfigurationProperties(prefix = "bluesky-boot.web.locale-resolve-handler")
public class LocaleResolveHandlerProperties implements BlueskyProperties {
	
	private static final long serialVersionUID = 1L;
	
	public static final String ACCEPT_HEADER_BEAN_NAME = "acceptHeaderLocaleResolveHandlerProperties";
	public static final String COOKIE_BEAN_NAME = "cookieLocaleResolveHandlerProperties";
	public static final String EXTERNAL_COOKIE_BEAN_NAME = "externalCookieLocaleResolveHandlerProperties";
	
	private String beanName;

	/**
	 * localeResolve 처리 조건
	 * COOKIE : 내부 쿠키 용 설정
	 * EXTERNAL_COOKIE : 외부 쿠키 용 설정, 유저가 의도한 변경에 대해서만 쿠키를 생성
	 * ACCEPT_HEADER : accept header 의 경우 language를 기준으로 사용
	 */
	private LocaleResolveInfoCondition localeResolveInfoCondition;
	
	/**
	 * 대표 로케일 지정 조건
	 * NONE : 대표 로케일로 지정하지 않음
	 * OVERWRITE : 무조건 지정,
	 * OVERWRITE_WHEN_NOT_EXISTS : 이미 존재하는 대표 로케일의 resolveLocale이 없으면 지정
	 * SKIP_IF_EXISTS : 이미 존재하는 대표 로케일이 있으면 넘어감 (이렇게 쓸일은 없을 듯)
	 */
	private SetRepresentativeCondition setRepresentativeCondition;
	
	/**
	 * 앞서 계산된 handler의 resolveLocaleContext 참조 조건
	 * NONE : 참조하지 않음
	 * USE_FIRST : 선행 handler의 resovleContext 값을 우선 참조
	 * USE_WHEN_NOT_RESOLVED : resolveLocale이 없는 경우 선행 handler의 resolveContext값을 참조
	 * USE_LANGUAGE_FIRST : 선행 handler의 resolveContext 값의 언어를 기준으로 우선 참조
	 * USE_LANGUAGE_WHEN_NOT_RESOLVED : resolveLocale이 없는 경우 선행 handler의 resolveContext값의 언어를 참조  
	 */
	private PreLocaleResolveInfoCondition preLocaleResolveInfoCondition;
	
	@AllArgsConstructor
	@Getter
	public enum LocaleResolveInfoCondition {
		COOKIE(false, true, true),
		EXTERNAL_COOKIE(false, false, true),
		ACCEPT_HEADER(true, false, false)
		;
		private boolean checkLanguageMatchOnly;
		private boolean resolveLocaleCookieCreate;
		private boolean setLocaleCookieCreate;
		
	}
	
	public enum SetRepresentativeCondition {
		NONE,
		OVERWRITE,
		OVERWRITE_IF_NOT_EXISTS,
	}
	
	@AllArgsConstructor
	@Getter
	public enum PreLocaleResolveInfoCondition {
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
