package io.github.luversof.boot.web;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
@ConfigurationProperties(prefix = "bluesky-boot.web.domain")
public class DomainProperties implements BlueskyProperties {
	
	/**
	 * When using multi-modules, if you make an add-path-pattern declaration, you need an addPathPatterns declaration for each module.
	 * 
	 * Registering the AntPathMatcher Pattern
	 */
	@Builder.Default
	private String[] addPathPatterns = new String[]{};

	/**
	 * If the site address is multi, use.
	 * 
	 * If your site doesn't have a PC web/mobile web distinction, declare only WEB and use it.
	 */
	@Builder.Default
	private List<URI> webList = new ArrayList<>();
	
	/**
	 * Use the mobile web address for your site if it is multi-use
	 * 
	 * Mobile web address must be declared if there is a PC web/mobile web distinction
	 * 
	 * Used separately in unified notification processing
	 */
	@Builder.Default
	private List<URI> mobileWebList = new ArrayList<>();
	
	/**
	 * When declared and used by domain in a multi-module project, the domain is a variable for local development that is set aside for development purposes.
	 * 
	 * You can use development domains in the form of a list by declaring them in succession with ",".
	 * 
	 * ex) devDomainList=http://local.a.com,http://local.b.com
	 */
	@Builder.Default
	private List<URI> devDomainList = new ArrayList<>();

	/**
	 * List of static paths (not redirect targets)
	 */
	@Builder.Default
	private List<String> staticPathList = Arrays.asList("/css/", "/html/", "/js/", "/img/", "/message/", "/favicon.ico", "/monitor/", "/support/");
	
	/**
	 * List of exception paths (list that are not redirect targets)
	 */
	@Builder.Default
	private List<String> excludePathList = Arrays.asList("/UiDev/", "/_check");
	
	/**
	 * Set the request root path
	 * 
	 * Default is "/" (full)
	 */
	@Builder.Default
	private String requestPath = "/";
	
	/**
	 * Set the forward root path
	 */
	@Builder.Default
	private String forwardPath = "/";
	
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
