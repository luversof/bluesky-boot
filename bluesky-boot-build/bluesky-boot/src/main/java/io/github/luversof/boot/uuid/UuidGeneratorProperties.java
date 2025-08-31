package io.github.luversof.boot.uuid;

import java.util.function.BiConsumer;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.BlueskyProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = UuidGeneratorProperties.PREFIX)
public class UuidGeneratorProperties implements BlueskyProperties {
	
	private static final long serialVersionUID = 1L;
	
	public static final String PREFIX = "bluesky-boot.uuid-generator";

	private UuidVersion version = UuidVersion.V7;
	
	protected BiConsumer<UuidGeneratorProperties, UuidGeneratorPropertiesBuilder> getPropertyMapperConsumer() {
		return (properties, builder) -> {
			var propertyMapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
			propertyMapper.from(properties::getVersion).to(builder::version);
		};
	}
	
	@Override
	public void load() {
		var blueskyBootContext = BlueskyBootContextHolder.getContext();
		var parentModuleInfo = blueskyBootContext.getParentModuleInfo();
		
		var builder = parentModuleInfo == null ? UuidGeneratorProperties.builder() : parentModuleInfo.getUuidGeneratorPropertiesBuilder();
		
		getPropertyMapperConsumer().accept(this, builder);
		
		BeanUtils.copyProperties(builder.build(), this);
	}
	
	
	public enum UuidVersion {
		V1, V4, V6, V7
	}
	
	public static UuidGeneratorPropertiesBuilder builder() {
		return new UuidGeneratorPropertiesBuilder();
	}
	
	public static class UuidGeneratorPropertiesBuilder {
		
		private UuidVersion version = UuidVersion.V7;
		
		public UuidGeneratorPropertiesBuilder version(UuidVersion version) {
			this.version = version;
			return this;
		}
		
		public UuidGeneratorProperties build() {
			return new UuidGeneratorProperties(this.version);
		}
	}

}
