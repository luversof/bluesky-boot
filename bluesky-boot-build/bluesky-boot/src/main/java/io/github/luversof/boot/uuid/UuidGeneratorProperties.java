package io.github.luversof.boot.uuid;

import java.util.function.BiConsumer;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.AbstractBlueskyProperties;
import io.github.luversof.boot.core.BlueskyPropertiesBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ConfigurationProperties(prefix = UuidGeneratorProperties.PREFIX)
public class UuidGeneratorProperties extends AbstractBlueskyProperties<UuidGeneratorProperties, UuidGeneratorProperties.UuidGeneratorPropertiesBuilder> {
	
	private static final long serialVersionUID = 1L;
	
	public static final String PREFIX = "bluesky-boot.uuid-generator";

	private UuidVersion version = UuidVersion.V7;
	
	protected BiConsumer<UuidGeneratorProperties, UuidGeneratorPropertiesBuilder> getPropertyMapperConsumer() {
		return (properties, builder) -> {
			if (properties == null) {
				return;
			}
			var propertyMapper = PropertyMapper.get();
			propertyMapper.from(properties::getVersion).to(builder::version);
		};
	}
	
	@Override
	protected UuidGeneratorPropertiesBuilder getBuilder() {
		var blueskyBootContext = BlueskyBootContextHolder.getContext();
		var parentModuleInfo = blueskyBootContext.getParentModuleInfo();
		return parentModuleInfo == null ? UuidGeneratorProperties.builder() : parentModuleInfo.getUuidGeneratorPropertiesBuilder();
	}
	
	public enum UuidVersion {
		V1, V4, V6, V7
	}
	
	public static UuidGeneratorPropertiesBuilder builder() {
		return new UuidGeneratorPropertiesBuilder();
	}
	
	public static class UuidGeneratorPropertiesBuilder implements BlueskyPropertiesBuilder<UuidGeneratorProperties> {
		
		private UuidVersion version = UuidVersion.V7;
		
		public UuidGeneratorPropertiesBuilder version(UuidVersion version) {
			this.version = version;
			return this;
		}
		
		@Override
		public UuidGeneratorProperties build() {
			return new UuidGeneratorProperties(this.version);
		}
	}

}
