package io.github.luversof.boot.autoconfigure.core.config;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import io.github.luversof.boot.autoconfigure.core.constant.CoreModuleInfo;
import io.github.luversof.boot.config.BlueskyCoreModuleProperties;
import io.github.luversof.boot.config.BlueskyCoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ConfigurationProperties(prefix = "bluesky-modules.core")
public class CoreProperties implements BlueskyCoreProperties<io.github.luversof.boot.autoconfigure.core.config.CoreProperties.CoreModuleProperties>, InitializingBean {

	private Map<String, CoreModuleProperties> modules = new HashMap<>();
	
	/**
	 * module 호출 기준 정의 <br />
	 * [domain (default), addPathPattern, moduleNameResolver]
	 */
	private CoreModulePropertiesResolveType resolveType = CoreModulePropertiesResolveType.DOMAIN;   


	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CoreModuleProperties implements BlueskyCoreModuleProperties {
		
		private CoreModuleInfo coreModuleInfo;
		
		public void setCoreModuleInfo(String coreModuleInfo) {
			this.coreModuleInfo = (new SpelExpressionParser()).parseExpression(coreModuleInfo).getValue(CoreModuleInfo.class);
		}
		
		/**
		 * 멀티 모듈 사용시 add-path-pattern 선언을 한 경우 각 모듈별 addPathPatterns 선언이 필요.<br />
		 * AntPathMatcher 패턴 등록
		 */
		@Builder.Default
		private String[] addPathPatterns = new String[]{};
		
		/**
		 * 해당 사이트의 도메인 정보 <br />
		 * ex) http://test.bluesky.net
		 */
		@Builder.Default
		private CoreDomainProperties domain = new CoreDomainProperties();
		
		/**
		 * 지원하지 않는 브라우저 체크 여부
		 */
		@Builder.Default
		private Boolean checkNotSupportedBrowser = true;
		
		/**
		 * 지원하지 않는 브라우저 체크 패턴
		 */
		@Builder.Default
		private String notSupportedBrowserRegPattern = ".*(MSIE [5-9]).*";
		
		/**
		 * 지원하지 않는 브라우저 체크 시 예외 주소 패턴 등록
		 */
		@Builder.Default
		private String[] notSupportedBrowserExcludePathPatterns = new String[]{"/css/**", "/html/**", "/js/**", "/img/**", "/message/**", "/favicon.ico", "/monitor/**", "/support/**", "/error/**"};
		
		/**
		 * 기본 설정 로케일
		 */
		private Locale defaultLocale;
		
		/**
		 * 사용 가능 로케일 목록
		 */
		private List<Locale> enableLocaleList;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (this.getModules().isEmpty()) {
			this.getModules().put("defaultModule", new CoreModuleProperties());
		}
	}
}
