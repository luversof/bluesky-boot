//package io.github.luversof.boot.context;
//
//import io.github.luversof.boot.config.BlueskyCoreModuleProperties;
//import io.github.luversof.boot.config.BlueskyCoreProperties;
//import io.github.luversof.boot.config.BlueskyProperties;
//import io.github.luversof.boot.util.ApplicationContextUtil;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Data
//@NoArgsConstructor
//public class BlueskyContextImpl implements BlueskyContext {
//	
//	private String moduleName;
//	
//	public BlueskyContextImpl(String moduleName) {
//		this.moduleName = moduleName;
//	}
//	
//	@Override
//	public <T, U extends BlueskyProperties<T>> T getModule(Class<U> u) {
//		U blueskyProperties = ApplicationContextUtil.getApplicationContext().getBean(u);
//		return blueskyProperties.getModules().get(moduleName);
//	}
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public <T extends BlueskyCoreModuleProperties> T getCoreModule() {
//		return (T) getModule(BlueskyCoreProperties.class);
//	}
//	
//}
