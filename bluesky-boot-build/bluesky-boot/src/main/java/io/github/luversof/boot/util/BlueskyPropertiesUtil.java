package io.github.luversof.boot.util;

import java.util.function.Supplier;

import org.springframework.context.ApplicationContext;
import org.springframework.core.ResolvableType;

import io.github.luversof.boot.context.BlueskyContextHolder;
import io.github.luversof.boot.core.BlueskyModuleProperties;
import io.github.luversof.boot.core.BlueskyProperties;
import lombok.experimental.UtilityClass;

@UtilityClass
public class BlueskyPropertiesUtil {
	
	private static final Supplier<ApplicationContext> ApplicationContextSupplier = ApplicationContextUtil::getApplicationContext;

	public static <T extends BlueskyProperties> T getProperties(Class<T> clazz) {
		
		var applicationContext = ApplicationContextSupplier.get();
		var moduleName = BlueskyContextHolder.getContext().getModuleName();
		if (moduleName == null) {
			return applicationContext.getBean(clazz);
		}
		var resolvableType =  ResolvableType.forClassWithGenerics(BlueskyModuleProperties.class, clazz);
		
		@SuppressWarnings("unchecked")
		var blueskyModuleProperties =  (BlueskyModuleProperties<T>) applicationContext.getBeanProvider(resolvableType).getIfAvailable();
		if (blueskyModuleProperties == null || blueskyModuleProperties.getModules() == null) {
			return null;
		}
		return blueskyModuleProperties.getModules().getOrDefault(moduleName, null);
	}

} 