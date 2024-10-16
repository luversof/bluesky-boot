package io.github.luversof.boot.core;

import java.util.HashMap;
import java.util.Map;

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
	 * 개별적으로 선언해서 사용하고자 하는 변수가 있는 경우 저장하는 map <br />
	 * parent의 값을 module이 상속받아 사용함 
	 */
	@Builder.Default
	private Map<String, String> properties = new HashMap<>();
	
	@SuppressWarnings("unchecked")
	public <T> T getProperties(String key) {
		return (T) properties.get(key);
	}
	
	/**
	 * Handling coreModuleInfo specification set by a spel expression
	 * @param moduleInfo Location of the generated coreModuleInfo, written as a SpEL expression
	 */
	public void setModuleInfo(String moduleInfo) {
		this.moduleInfo = (new SpelExpressionParser()).parseExpression(moduleInfo).getValue(ModuleInfo.class);
	}
	
	@Override
	public void setCoreProperties(CoreProperties coreProperties) {
		// DO NOTHING
	}
	
}
