//package io.github.luversof.boot.core;
//
//import java.net.URI;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Locale;
//
//import lombok.Data;
//
//public interface BlueskyCoreModuleProperties {
//	
//	/**
//	 * Domain information for the site
//	 * 
//	 * ex) http://test.bluesky.net
//	 */
//	void setDomain(CoreDomainProperties coreDomainProperties);
//	CoreDomainProperties getDomain();
//	
//	/**
//	 * When using multi-modules, if you make an add-path-pattern declaration, you need an addPathPatterns declaration for each module.
//	 * 
//	 * AntPathMatcher 패턴 등록
//	 */
//	void setAddPathPatterns(String[] addPathPatterns);
//	String[] getAddPathPatterns();
//
//	/**
//	 * Setting the default locale
//	 */
//	void setDefaultLocale(Locale defaultLocale);
//	Locale getDefaultLocale();
//	
//	/**
//	 * List of available locales
//	 */
//	void setEnableLocaleList(List<Locale> enableLocaleList);
//	List<Locale> getEnableLocaleList();
//	
//
//	@Data
//	public static class PathForwardProperties {
//
//		/**
//		 * List of static paths (not redirect targets)
//		 */
//		private List<String> staticPathList = Arrays.asList("/css/", "/html/", "/js/", "/img/", "/message/", "/favicon.ico", "/monitor/", "/support/");
//		
//		/**
//		 * List of exception paths (list that are not redirect targets)
//		 */
//		private List<String> excludePathList = Arrays.asList("/UiDev/", "/_check");
//		
//		/**
//		 * Set the request root path
//		 * 
//		 * Default is "/" (full)
//		 */
//		private String requestPath = "/";
//		
//		/**
//		 * Set the forward root path
//		 */
//		private String forwardPath = "/";
//	}
//}
