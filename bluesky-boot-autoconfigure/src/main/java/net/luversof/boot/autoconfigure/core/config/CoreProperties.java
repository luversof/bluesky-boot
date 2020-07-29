package net.luversof.boot.autoconfigure.core.config;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.luversof.boot.autoconfigure.core.constant.CoreModuleInfo;
import net.luversof.boot.core.config.BlueskyProperties;

@Data
@ConfigurationProperties(prefix = "bluesky-modules.core")
public class CoreProperties
		implements BlueskyProperties<net.luversof.boot.autoconfigure.core.config.CoreProperties.CoreModuleProperties>, InitializingBean {

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
	public static class CoreModuleProperties {
		
		private CoreModuleInfo coreModuleInfo;
		
		public void setCoreModuleInfo(String coreModuleInfo) {
			this.coreModuleInfo = (new SpelExpressionParser()).parseExpression(coreModuleInfo).getValue(CoreModuleInfo.class);
		}
		
		/**
		 * 멀티 모듈 사용시 brick-modules.core.resolve-type=add-path-pattern 선언을 한 경우 각 모듈별 addPathPatterns 선언이 필요.<br />
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
	
	/**
	 * 해당 사이트가 사용하는 도메인 설정
	 * @author bluesky
	 *
	 */
	@Data
	public static class CoreDomainProperties {
		
		/**
		 * 해당 사이트 주소를 멀티로 사용하는 경우 사용<br />
		 * 만약 pc 웹/ 모바일 웹 구분이 없는 사이트인 경우 web만 선언하여 사용
		 */
		private List<URI> webList = new ArrayList<>();
		
		/**
		 * 해당 사이트의 모바일 웹 주소를 멀티로 사용하는 경우 사용<br />
		 * pc 웹/ 모바일 웹 구분이 있는 경우 모바일 웹 주소를 선언해야 함<br />
		 * 통합 알림 처리에서 구분해서 사용함
		 */
		private List<URI> mobileWebList = new ArrayList<>();
		
		/**
		 * 멀티 모듈 프로젝트에서 domain 기준으로 선언해서 사용하는 경우 로컬 개발 시 도메인은 개발용으로 따로 관리하기 위한 변수 <br />
		 * list형태로 개발용 도메인을 ","로 연이어 선언하여 사용할 수 있음 <br />
		 * ex) devDomainList=local.a.plaync.com,local.b.plaync.com
		 */
		private List<URI> devDomainList = new ArrayList<>();

		/**
		 * 해당 모듈에서 전역으로 사용하는 forward 정책이 있는 경우 설정
		 */
		private PathForwardProperties pathForward;
		
		/**
		 * 해당 사이트 주소<br />
		 * 만약 pc 웹/ 모바일 웹 구분이 없는 사이트인 경우 web만 선언하여 사용
		 */
		public void setWeb(URI uri) {
			if (this.webList.contains(uri)) {
				return;
			}
			this.webList.add(uri);
		}
		
		/**
		 * 해당 사이트의 모바일 웹 주소<br />
		 * pc 웹/ 모바일 웹 구분이 있는 경우 모바일 웹 주소를 선언해야 함<br />
		 * 통합 알림 처리에서 구분해서 사용함
		 */
		public void setMobileWeb(URI uri) {
			if (this.mobileWebList.contains(uri)) {
				return;
			}
			this.mobileWebList.add(uri);
		}
		
		public URI getWeb() {
			// 만약 현재 DeviceType에 대한 도메인을 획득하는 로직이 필요한 경우 여기에 추가 개발 해야함
			return this.getWebList().isEmpty() ? null : this.getWebList().get(0);
		}
		
		public URI getMobileWeb() {
			// 만약 현재 DeviceType에 대한 도메인을 획득하는 로직이 필요한 경우 여기에 추가 개발 해야함
			return mobileWebList.isEmpty() ? getWeb() : mobileWebList.get(0);
		}
	}
	
	@Data
	public static class PathForwardProperties {

		/**
		 * static path 목록 (redirect 대상이 아닌 목록)
		 */
		private List<String> staticPathList = Arrays.asList("/css/", "/html/", "/js/", "/img/", "/message/", "/favicon.ico", "/monitor/", "/support/");
		
		/**
		 * 예외 path 목록 (redirect 대상이 아닌 목록)
		 */
		private List<String> excludePathList = Arrays.asList("/UiDev/", "/_check");
		
		/**
		 * 요청 root path 설정
		 * 기본은 "/" (전체)
		 */
		private String requestPath = "/";
		
		/**
		 * forward root path 설정
		 */
		private String forwardPath = "/";
	}

	public enum CoreModulePropertiesResolveType {

		/**
		 * 도메인 기준 module 분기 처리 시 사용
		 */
		DOMAIN,

		/**
		 * request path 기준으로 분기 처리 시 사용
		 */
		ADD_PATH_PATTERN,

		/**
		 * 별도 resolver를 구현한 경우 사용 <br />
		 * 도메인을 사용하지 않는 API 서버와 같은 경우 추가 구현하여 분기 처리를 할 수 있도록 제공함
		 */
		MODULE_NAME_RESOLVER;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (this.getModules().isEmpty()) {
			this.getModules().put("defaultModule", new CoreModuleProperties());
		}
	}
}
