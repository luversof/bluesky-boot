package net.luversof.boot.context;

import lombok.Data;
import lombok.NoArgsConstructor;
import net.luversof.boot.config.BlueskyCoreModuleProperties;
import net.luversof.boot.config.BlueskyCoreProperties;
import net.luversof.boot.config.BlueskyProperties;
import net.luversof.boot.util.ApplicationContextUtil;

@Data
@NoArgsConstructor
public class BlueskyContextImpl implements BlueskyContext {
	
	private String moduleName;
	
	public BlueskyContextImpl(String moduleName) {
		this.moduleName = moduleName;
	}
	
	@Override
	public <T, U extends BlueskyProperties<T>> T getModule(Class<U> u) {
		U blueskyProperties = ApplicationContextUtil.getApplicationContext().getBean(u);
		return blueskyProperties.getModules().get(moduleName);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends BlueskyCoreModuleProperties> T getCoreModule() {
		return (T) getModule(BlueskyCoreProperties.class);
	}
	
}
