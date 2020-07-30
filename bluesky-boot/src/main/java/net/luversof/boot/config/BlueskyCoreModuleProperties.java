package net.luversof.boot.config;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import lombok.Data;

public interface BlueskyCoreModuleProperties {
	
	/**
	 * 해당 사이트의 도메인 정보 <br />
	 * ex) http://test.bluesky.net
	 */
	void setDomain(CoreDomainProperties coreDomainProperties);
	CoreDomainProperties getDomain();
	
	/**
	 * 멀티 모듈 사용시 add-path-pattern 선언을 한 경우 각 모듈별 addPathPatterns 선언이 필요.<br />
	 * AntPathMatcher 패턴 등록
	 */
	void setAddPathPatterns(String[] addPathPatterns);
	String[] getAddPathPatterns();

	/**
	 * 기본 로케일 설정
	 */
	void setDefaultLocale(Locale defaultLocale);
	Locale getDefaultLocale();
	
	/**
	 * 사용 가능 로케일 목록
	 */
	void setEnableLocaleList(List<Locale> enableLocaleList);
	List<Locale> getEnableLocaleList();
	

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
		 * ex) devDomainList=http://local.a.com,http://local.b.com
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
}
