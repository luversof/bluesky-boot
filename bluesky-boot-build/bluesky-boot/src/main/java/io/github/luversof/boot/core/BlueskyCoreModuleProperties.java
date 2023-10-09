package io.github.luversof.boot.core;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import lombok.Data;

public interface BlueskyCoreModuleProperties {
	
	/**
	 * Domain information for the site
	 * 
	 * ex) http://test.bluesky.net
	 */
	void setDomain(CoreDomainProperties coreDomainProperties);
	CoreDomainProperties getDomain();
	
	/**
	 * When using multi-modules, if you make an add-path-pattern declaration, you need an addPathPatterns declaration for each module.
	 * 
	 * AntPathMatcher 패턴 등록
	 */
	void setAddPathPatterns(String[] addPathPatterns);
	String[] getAddPathPatterns();

	/**
	 * Setting the default locale
	 */
	void setDefaultLocale(Locale defaultLocale);
	Locale getDefaultLocale();
	
	/**
	 * List of available locales
	 */
	void setEnableLocaleList(List<Locale> enableLocaleList);
	List<Locale> getEnableLocaleList();
	

	/**
	 * Set the domain that the site uses
	 * @author bluesky
	 *
	 */
	@Data
	public static class CoreDomainProperties {
		
		/**
		 * If the site address is multi, use.
		 * 
		 * If your site doesn't have a PC web/mobile web distinction, declare only WEB and use it.
		 */
		private List<URI> webList = new ArrayList<>();
		
		/**
		 * Use the mobile web address for your site if it is multi-use
		 * 
		 * Mobile web address must be declared if there is a PC web/mobile web distinction
		 * 
		 * Used separately in unified notification processing
		 */
		private List<URI> mobileWebList = new ArrayList<>();
		
		/**
		 * When declared and used by domain in a multi-module project, the domain is a variable for local development that is set aside for development purposes.
		 * 
		 * You can use development domains in the form of a list by declaring them in succession with ",".
		 * 
		 * ex) devDomainList=http://local.a.com,http://local.b.com
		 */
		private List<URI> devDomainList = new ArrayList<>();

		/**
		 * If you have a FORWARD policy that is used globally by that module, set it to Settings
		 */
		private PathForwardProperties pathForward;
		
		/**
		 * The address of the site
		 * 
		 * If your site doesn't have a PC web/mobile web distinction, declare only WEB and use the
		 */
		public void setWeb(URI uri) {
			if (this.webList.contains(uri)) {
				return;
			}
			this.webList.add(uri);
		}
		
		/**
		 * Mobile web address for your site
		 * 
		 * Mobile web address must be declared if there is a PC web/mobile web distinction
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
		 * List of static paths (not redirect targets)
		 */
		private List<String> staticPathList = Arrays.asList("/css/", "/html/", "/js/", "/img/", "/message/", "/favicon.ico", "/monitor/", "/support/");
		
		/**
		 * List of exception paths (list that are not redirect targets)
		 */
		private List<String> excludePathList = Arrays.asList("/UiDev/", "/_check");
		
		/**
		 * Set the request root path
		 * 
		 * Default is "/" (full)
		 */
		private String requestPath = "/";
		
		/**
		 * Set the forward root path
		 */
		private String forwardPath = "/";
	}
}
