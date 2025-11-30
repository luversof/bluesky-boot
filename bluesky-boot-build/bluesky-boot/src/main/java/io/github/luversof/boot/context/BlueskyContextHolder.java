package io.github.luversof.boot.context;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import io.github.luversof.boot.core.BlueskyGroupProperties;
import io.github.luversof.boot.core.BlueskyModuleProperties;
import io.github.luversof.boot.core.BlueskyProperties;
import io.github.luversof.boot.core.CoreProperties;
import io.github.luversof.boot.exception.BlueskyException;

/**
 * Holder of BlueskyContext
 * A holder created by referencing Spring Security's SecurityContextHolder
 */
public final class BlueskyContextHolder {
	
	private BlueskyContextHolder() {}
	
	public static final String MODE_THREADLOCAL = "MODE_THREADLOCAL";
	
	public static final String MODE_INHERITABLETHREADLOCAL = "MODE_INHERITABLETHREADLOCAL";
	
	public static final String MODE_GLOBAL = "MODE_GLOBAL";
	
	private static final String MODE_PRE_INITIALIZED = "MODE_PRE_INITIALIZED";
	
	public static final String SYSTEM_PROPERTY = "bluesky.context.strategy";
	
	private static String strategyName = System.getProperty(SYSTEM_PROPERTY);
	
	private static BlueskyContextHolderStrategy strategy;
	
	private static int initializeCount = 0;
	
	static {
		initialize();
	}
	
	private static void initialize() {
		initializeStrategy();
		initializeCount++;
	}
	
	private static void initializeStrategy() {
		if (MODE_PRE_INITIALIZED.equals(strategyName)) {
			Assert.state(strategy != null, "When using " + MODE_PRE_INITIALIZED
					+ ", setContextHolderStrategy must be called with the fully constructed strategy");
			return;
		}
		if (!StringUtils.hasText(strategyName)) {
			// Set default
			strategyName = MODE_THREADLOCAL;
		}
		
		if (strategyName.equals(MODE_THREADLOCAL)) {
			strategy = new ThreadLocalBlueskyContextHolderStrategy();
			return;
		}
		if (strategyName.equals(MODE_INHERITABLETHREADLOCAL)) {
			strategy = new InheritableThreadLocalBlueskyContextHolderStrategy();
			return;
		}
		if (strategyName.equals(MODE_GLOBAL)) {
			strategy = new GlobalBlueskyContextHolderStrategy();
			return;
		}
		// Try to load a custom strategy
		try {
			var clazz = Class.forName(strategyName);
			var customStrategy = clazz.getConstructor();
			strategy = (BlueskyContextHolderStrategy) customStrategy.newInstance();
		}
		catch (Exception ex) {
			ReflectionUtils.handleReflectionException(ex);
		}
	}

	/**
	 * clear context
	 */
	public static void clearContext() {
		strategy.clearContext();
	}

	/**
	 * get context
	 * @return BlueskyContext
	 */
	public static BlueskyContext getContext() {
		return strategy.getContext();
	}
	
	/**
	 * get initializeCount
	 * @return initializeCount
	 */
	public static int getInitializeCount() {
		return initializeCount;
	}
	
	/**
	 * set context
	 * @param context BlueskyContext
	 */
	public static void setContext(BlueskyContext context) {
		strategy.setContext(context);
	}
	
	/**
	 * set context
	 * @param moduleName moduleName to set in context
	 */
	public static void setContext(String moduleName) {
		setContext(() -> moduleName);
	}
	
	/**
	 * Delegates the creation of a new, empty context to the configured strategy.
	 */
	public static BlueskyContext createEmptyContext() {
		return strategy.createEmptyContext();
	}
	
	@Override
	public String toString() {
		return "BlueskyContextHolder[strategy='" + strategy.getClass().getSimpleName() + "'; initializeCount="
				+ initializeCount + "]";
	}
	
	/**
	 * 현재 module에 대한 Properties 반환
	 * 
	 * @param <T> T extends BlueskyProperties
	 * @param blueskyPropertiesClass blueskyProperties extension class
	 * @return BlueskyProperties extension object
	 */
	public static <T extends BlueskyProperties> T getProperties(Class<T> blueskyPropertiesClass) {
		return getProperties(blueskyPropertiesClass, null);
	}
	
	/**
	 * 같은 class로 여러 properties object를 사용하는 경우 지정된 bean name의 properties object를 호출
	 * 
	 * @param <T> T extends BlueskyProperties
	 * @param blueskyPropertiesClass blueskyProperties extension class
	 * @param blueskyPropertiesBeanName blueskyProperties bean name
	 * @return BlueskyProperties extension object
	 */
	public static <T extends BlueskyProperties> T getProperties(Class<T> blueskyPropertiesClass, String blueskyPropertiesBeanName) {
		var blueskyModuleProperties = getModuleProperties(blueskyPropertiesClass, blueskyPropertiesBeanName);
		
		// moduleBeanNamesForType가 없으면 BlueskyProperties를 직접 조회
		if (blueskyModuleProperties == null) {
			var applicationContext = ApplicationContextUtil.getApplicationContext();
			if (blueskyPropertiesBeanName != null) {	// 지정된 BlueskyPropertiesBeanName이 없으면 첫번째 beanName을 사용
				return applicationContext.getBean(blueskyPropertiesBeanName, blueskyPropertiesClass);
			}
			
			String[] beanNamesForType = applicationContext.getBeanNamesForType(blueskyPropertiesClass);
			if (beanNamesForType.length == 0) {
				throw new BlueskyException("NOT EXIST TARGET BLUESKY PROPERTIES");
			} 
			return applicationContext.getBean(beanNamesForType[0], blueskyPropertiesClass);
		}
				
		return getProperties(blueskyModuleProperties);
	}
	
	
	private static <T extends BlueskyProperties, U extends BlueskyModuleProperties<T>> U getModuleProperties(Class<T> blueskyPropertiesClass, String blueskyPropertiesBeanName) {
		var moduleResolvableType = ResolvableType.forClassWithGenerics(BlueskyModuleProperties.class, blueskyPropertiesClass);
		var applicationContext = ApplicationContextUtil.getApplicationContext();
		
		String[] moduleBeanNamesForType = applicationContext.getBeanNamesForType(moduleResolvableType);
		if (moduleBeanNamesForType.length == 0) {
			return null;
		}
		
		
		ObjectProvider<U> moduleBeanProvider = applicationContext.getBeanProvider(moduleResolvableType);
		if (blueskyPropertiesBeanName == null) {
			return moduleBeanProvider.orderedStream().toList().get(0);
		}
		var parent = applicationContext.getBean(blueskyPropertiesBeanName, blueskyPropertiesClass);
		return moduleBeanProvider.stream().filter(x-> x.getParent() == parent).findFirst().orElseThrow(() -> new BlueskyException("NOT EXIST TARGET BLUESKY MODULE PROPERTIES"));
	}
	
	private static <T extends BlueskyProperties, U extends BlueskyModuleProperties<T>> T getProperties(U blueskyModuleProperties) {
		var moduleName = getContext().getModuleName();
		if (moduleName == null || blueskyModuleProperties.getModules() == null || !blueskyModuleProperties.getModules().containsKey(moduleName)) {
			return blueskyModuleProperties.getParent();
		}
		return blueskyModuleProperties.getModules().get(moduleName);
	}
	
	public static <T extends BlueskyProperties> BlueskyGroupProperties<T> getGroupProperties(Class<T> blueskyPropertiesClass, String blueskyPropertiesBeanName) {
		var groupResolvableType = ResolvableType.forClassWithGenerics(BlueskyGroupProperties.class, blueskyPropertiesClass);
		var applicationContext = ApplicationContextUtil.getApplicationContext();
		
		String[] groupBeanNamesForType = applicationContext.getBeanNamesForType(groupResolvableType);
		if (groupBeanNamesForType.length == 0) {
			return null;
		}
		
		ObjectProvider<BlueskyGroupProperties<T>> groupBeanProvider = applicationContext.getBeanProvider(groupResolvableType);
		
		if (blueskyPropertiesBeanName == null) {
			return groupBeanProvider.orderedStream().toList().get(0);
		}
		var parent = applicationContext.getBean(blueskyPropertiesBeanName, blueskyPropertiesClass);
		return groupBeanProvider.stream().filter(x -> x.getParent() == parent).findFirst().orElseGet(() -> null);
	}
	
	/**
	 * coreProperties의 경우 가장 자주 쓰이기 때문에 기본 제공
	 * @return CoreProperties
	 */
	public static CoreProperties getCoreProperties() {
		return getProperties(CoreProperties.class);	
	}
}
