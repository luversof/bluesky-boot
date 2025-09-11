package io.github.luversof.boot.core;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Configuration properties for Core.
 * @author bluesky
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = CoreProperties.PREFIX)
public class CoreProperties extends AbstractBlueskyProperties<CoreProperties, CoreProperties.CorePropertiesBuilder> {
	
	private static final long serialVersionUID = 1L;
	
	public static final String PREFIX = "bluesky-boot.core";
	
	/**
	 * Bean 생성 시 지정할 이름
	 */
	public static final String BEAN_NAME = "blueskyCoreProperties";
	
	private ModuleInfo moduleInfo;
	
	/**
	 * 개별적으로 선언해서 사용하고자 하는 변수가 있는 경우 저장하는 map <br />
	 * parent의 값을 module이 상속받아 사용함 
	 */
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
	protected BiConsumer<CoreProperties, CorePropertiesBuilder> getPropertyMapperConsumer() {
		return (prop, builder) -> {
			if (prop == null) {
				return;
			}
			var propertyMapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
			propertyMapper.from(prop::getModuleInfo).to(builder::moduleInfo);
			propertyMapper.from(prop::getProperties).to(builder::properties);
		};
	}
	
	@Override
	public void load() {
		BlueskyBootContextHolder.getContext().setParentModuleInfo(getModuleInfo());
		super.load();
	}
	
	@Override
	protected CorePropertiesBuilder getBuilder() {
		var blueskyBootContext = BlueskyBootContextHolder.getContext();
		var parentModuleInfo = blueskyBootContext.getParentModuleInfo();
		return parentModuleInfo == null ? CoreProperties.builder() : parentModuleInfo.getCorePropertiesBuilder();
	}

	public static CorePropertiesBuilder builder() {
		return new CorePropertiesBuilder();
	}
	
	@NoArgsConstructor(access = AccessLevel.NONE)
	public static class CorePropertiesBuilder implements BlueskyPropertiesBuilder<CoreProperties> {
		
		private ModuleInfo moduleInfo;
		
		private Map<String, String> properties = new HashMap<>();
		
		public CorePropertiesBuilder moduleInfo(ModuleInfo moduleInfo) {
			this.moduleInfo = moduleInfo;
			return this;
		}
		
		public CorePropertiesBuilder properties(Map<String, String> properties) {
			this.properties.putAll(properties);
			return this;
		}
		
		@Override
		public CoreProperties build() {
			return new CoreProperties(
				this.moduleInfo,
				this.properties == null ? new HashMap<>() : this.properties
			);
		}
	}
	
}
