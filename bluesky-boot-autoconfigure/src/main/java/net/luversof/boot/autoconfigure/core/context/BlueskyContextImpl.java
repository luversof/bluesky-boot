package net.luversof.boot.autoconfigure.core.context;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.luversof.boot.autoconfigure.core.config.CoreProperties;
import net.luversof.boot.autoconfigure.core.config.CoreProperties.CoreModuleProperties;
import net.luversof.boot.autoconfigure.core.util.ApplicationContextUtil;
import net.luversof.boot.core.config.BlueskyProperties;

@Data
@NoArgsConstructor
public class BlueskyContextImpl implements BlueskyContext {
	
	private String moduleName;
	
	public BlueskyContextImpl(String moduleName) {
		this.moduleName = moduleName;
	}
	
	@Override
	public <T, U extends BlueskyProperties<T>> T getModule(Class<U> u) {
		U brickProperties = ApplicationContextUtil.getApplicationContext().getBean(u);
		return brickProperties.getModules().get(moduleName);
	}

	@Override
	public CoreModuleProperties getCoreModule() {
		return getModule(CoreProperties.class);
	}
	
}
