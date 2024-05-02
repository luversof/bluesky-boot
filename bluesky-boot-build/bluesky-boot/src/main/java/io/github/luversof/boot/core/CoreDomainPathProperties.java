package io.github.luversof.boot.core;

import java.util.Arrays;
import java.util.List;

import lombok.Data;

@Data
public class CoreDomainPathProperties {

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