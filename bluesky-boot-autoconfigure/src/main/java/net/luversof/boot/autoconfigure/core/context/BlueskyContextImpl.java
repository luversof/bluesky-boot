package net.luversof.boot.autoconfigure.core.context;

import java.util.Map;

import lombok.Data;
import net.luversof.boot.autoconfigure.core.config.BlueskyProperties;

@Data
public class BlueskyContextImpl implements BlueskyContext {
	
	private String moduleName;
	
	private Map<Class<? extends BlueskyProperties<?>>, BlueskyProperties<?>> blueskyPropertiesMap;
	
}
