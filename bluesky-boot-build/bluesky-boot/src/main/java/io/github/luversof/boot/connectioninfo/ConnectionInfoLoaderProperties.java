package io.github.luversof.boot.connectioninfo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@ConfigurationProperties(prefix = "bluesky-boot.connection-info")
public class ConnectionInfoLoaderProperties {
	
	private Map<String, LoaderInfo> loaders = new HashMap<>();
	
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class LoaderInfo {
		
		/**
		 * 해당 Loader를 사용할지 여부를 설정 
		 */
		private boolean enabled;
		
		/**
		 * loader 호출 정보 관리
		 * 현재는 비정형화 하여 제공.
		 * 추후 정형화가 가능해지면 변경도 고려할 수 있으나 그럴일이 있을까?
		 */
		private Map<String, String> properties;
		
		/**
		 * 사용할 connection 목록
		 * 사용자가 사용할 connection을 설정
		 */
		private Map<String, List<String>> connections;
	}

}