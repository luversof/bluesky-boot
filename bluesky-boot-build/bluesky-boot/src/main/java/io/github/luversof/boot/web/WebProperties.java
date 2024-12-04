package io.github.luversof.boot.web;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.core.BlueskyProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "bluesky-boot.web")
public class WebProperties implements BlueskyProperties {
	
	private static final long serialVersionUID = 1L;

	private String beanName;
	
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
