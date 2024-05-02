package io.github.luversof.boot.core;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.expression.spel.standard.SpelExpressionParser;

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
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "bluesky-boot.core")
public class CoreProperties implements BlueskyProperties {

	private ModuleInfo moduleInfo;
	
	/**
	 * Handling coreModuleInfo specification set by a spel expression
	 * @param moduleInfo Location of the generated coreModuleInfo, written as a SpEL expression
	 */
	public void setModuleInfo(String moduleInfo) {
		this.moduleInfo = (new SpelExpressionParser()).parseExpression(moduleInfo).getValue(ModuleInfo.class);
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
	
}
