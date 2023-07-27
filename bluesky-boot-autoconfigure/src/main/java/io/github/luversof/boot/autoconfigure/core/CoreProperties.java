package io.github.luversof.boot.autoconfigure.core;

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

/**
 * Configuration properties for Core.
 * @author bluesky
 *
 */
@Data
@ConfigurationProperties(prefix = "bluesky-boot.core")
public class CoreProperties implements BlueskyCoreProperties<io.github.luversof.boot.autoconfigure.core.CoreProperties.CoreModuleProperties>, InitializingBean {

	private Map<String, CoreModuleProperties> modules = new HashMap<>();
	
	/**
	 * Define module invocation criteria
	 * 
	 * [domain (default), addPathPattern, moduleNameResolver]
	 */
	private CoreModulePropertiesResolveType resolveType = CoreModulePropertiesResolveType.DOMAIN;   


	/**
	 * Configuration module properties for Core.
	 * @author bluesky
	 *
	 */
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class CoreModuleProperties implements BlueskyCoreModuleProperties {
		
		private CoreModuleInfo coreModuleInfo;
		
		/**
		 * Handling coreModuleInfo specification set by a spel expression
		 * @param coreModuleInfo Location of the generated coreModuleInfo, written as a SpEL expression
		 */
		public void setCoreModuleInfo(String coreModuleInfo) {
			this.coreModuleInfo = (new SpelExpressionParser()).parseExpression(coreModuleInfo).getValue(CoreModuleInfo.class);
		}
		
		/**
		 * When using multi-modules, if you make an add-path-pattern declaration, you need an addPathPatterns declaration for each module.
		 * 
		 * Registering the AntPathMatcher Pattern
		 */
		@Builder.Default
		private String[] addPathPatterns = new String[]{};
		
		/**
		 * Domain information for the site
		 * 
		 * ex) http://test.bluesky.net
		 */
		@Builder.Default
		private CoreDomainProperties domain = new CoreDomainProperties();
		
		/**
		 * Whether to check unsupported browsers
		 */
		@Builder.Default
		private Boolean checkNotSupportedBrowser = true;
		
		/**
		 * Unsupported browser check patterns
		 */
		@Builder.Default
		private String notSupportedBrowserRegPattern = ".*(MSIE [5-9]).*";
		
		/**
		 * Registering an exception address pattern when checking for unsupported browsers
		 */
		@Builder.Default
		private String[] notSupportedBrowserExcludePathPatterns = new String[]{"/css/**", "/html/**", "/js/**", "/img/**", "/message/**", "/favicon.ico", "/monitor/**", "/support/**", "/error/**"};
		
		/**
		 * Preferred Locale
		 */
		private Locale defaultLocale;
		
		/**
		 * List of available locales
		 */
		private List<Locale> enableLocaleList;
		
		/**
		 * Builder class for providing CoreModuleInfo
		 * @author bluesky
		 *
		 */
		public static class CoreModulePropertiesBuilder {}
	}
	

	@Override
	public void afterPropertiesSet() throws Exception {
		if (this.getModules().isEmpty()) {
			this.getModules().put("defaultModule", new CoreModuleProperties());
		}
	}
}
