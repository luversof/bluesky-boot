//package io.github.luversof.boot.core;
//
//import org.springframework.core.ResolvableType;
//
//import io.github.luversof.boot.context.ApplicationContextUtil;
//import io.github.luversof.boot.context.BlueskyContextHolder;
//import lombok.experimental.UtilityClass;
//
//@UtilityClass
//public class BlueskyPropertiesUtil {
//	
//	public static <T extends BlueskyProperties> T getProperties(Class<T> clazz) {
//		var applicationContext = ApplicationContextUtil.getApplicationContext();
//		
//		var resolvableType =  ResolvableType.forClassWithGenerics(BlueskyModuleProperties.class, clazz);
//		@SuppressWarnings("unchecked")
//		var blueskyModuleProperties =  (BlueskyModuleProperties<T>) applicationContext.getBeanProvider(resolvableType).getIfUnique();
//		
//		var moduleName = BlueskyContextHolder.getContext().getModuleName();
//		if (moduleName == null) {
//			return applicationContext.getBean(clazz);
//		}
//		
//		if (blueskyModuleProperties == null || !blueskyModuleProperties.getModules().containsKey(moduleName)) {
//			return null;
//		}
//		return blueskyModuleProperties.getModules().getOrDefault(moduleName, null);
//	}
//
//} 