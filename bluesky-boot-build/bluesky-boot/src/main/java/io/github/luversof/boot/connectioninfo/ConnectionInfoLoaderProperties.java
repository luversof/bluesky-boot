package io.github.luversof.boot.connectioninfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * An object containing loader information and a list of connection targets to load.
 * @author bluesky
 */
@Data
@ConfigurationProperties(prefix = "bluesky-boot.connection-info")
public class ConnectionInfoLoaderProperties {
	
	/**
	 * Manage loader information for each loader and a list of connection targets to be called through the loader.
	 */
	private Map<String, LoaderInfo> loaders = new HashMap<>();
	
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class LoaderInfo {
		
		/**
		 * Whether to use this Loader or not
		 */
		private boolean enabled;
		
		/**
		 * Manage loader call information
		 * Currently used in an informal form.
		 */
		private Map<String, String> properties;
		
		/**
		 * List of connections to use
		 */
		private Map<String, List<String>> connections;
	}

}