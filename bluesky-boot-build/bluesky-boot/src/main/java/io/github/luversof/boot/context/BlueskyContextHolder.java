package io.github.luversof.boot.context;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import io.github.luversof.boot.core.BlueskyModuleProperties;
import io.github.luversof.boot.core.BlueskyProperties;
import io.github.luversof.boot.core.CoreProperties;
import io.github.luversof.boot.exception.BlueskyException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BlueskyContextHolder {
	
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

	public static void clearContext() {
		strategy.clearContext();
	}

	public static BlueskyContext getContext() {
		return strategy.getContext();
	}

	public static int getInitializeCount() {
		return initializeCount;
	}
	
	public static void setContext(BlueskyContext context) {
		strategy.setContext(context);
	}
	
	public static void setContext(String moduleName) {
		setContext(() -> moduleName);
	}
	
	public static void setStrategyName(String strategyName) {
		BlueskyContextHolder.strategyName = strategyName;
		initialize();
	}
	
	public static void setContextHolderStrategy(BlueskyContextHolderStrategy strategy) {
		Assert.notNull(strategy, "securityContextHolderStrategy cannot be null");
		BlueskyContextHolder.strategyName = MODE_PRE_INITIALIZED;
		BlueskyContextHolder.strategy = strategy;
		initialize();
	}
	
	public static BlueskyContextHolderStrategy getContextHolderStrategy() {
		return strategy;
	}

	public static BlueskyContext createEmptyContext() {
		return strategy.createEmptyContext();
	}
	
	@Override
	public String toString() {
		return "SecurityContextHolder[strategy='" + strategy.getClass().getSimpleName() + "'; initializeCount="
				+ initializeCount + "]";
	}
	
	/**
	 * 현재 module에 대한 Properties 반환
	 * @param <T>
	 * @param blueskyPropertiesClass
	 * @return
	 */
	public static <T extends BlueskyProperties> T getProperties(Class<T> blueskyPropertiesClass) {
		return getProperties(blueskyPropertiesClass, null);
	}
	
	/**
	 * 같은 class로 여러 properties object를 사용하는 경우 지정된 bean name의 properties object를 호출
	 * @param <T>
	 * @param blueskyPropertiesClass
	 * @param blueskyPropertiesBeanName
	 * @return
	 */
	public static <T extends BlueskyProperties> T getProperties(Class<T> blueskyPropertiesClass, String blueskyPropertiesBeanName) {
		var moduleResolvableType = ResolvableType.forClassWithGenerics(BlueskyModuleProperties.class, blueskyPropertiesClass);
		var applicationContext = ApplicationContextUtil.getApplicationContext();
		
		String[] moduleBeanNamesForType = applicationContext.getBeanNamesForType(moduleResolvableType);
		
		// moduleProperties가 없는 경우 처리
		if (moduleBeanNamesForType.length == 0) {
			if (blueskyPropertiesBeanName == null) {	// 지정된 BlueskyPropertiesBeanName이 없으면 첫번째 beanName을 사용
				String[] beanNamesForType = applicationContext.getBeanNamesForType(blueskyPropertiesClass);
				if (beanNamesForType.length == 0) {
					throw new BlueskyException("NOT EXIST TARGET BLUESKY PROPERTIES");
				} 
				return applicationContext.getBean(beanNamesForType[0], blueskyPropertiesClass);
			} else {
				return applicationContext.getBean(blueskyPropertiesBeanName, blueskyPropertiesClass);
			}
		}
		ObjectProvider<BlueskyModuleProperties<T>> moduleBeanProvider = applicationContext.getBeanProvider(moduleResolvableType);
		BlueskyModuleProperties<T> blueskyModuleProperties = null;
		if (blueskyPropertiesBeanName == null) {
			blueskyModuleProperties =  moduleBeanProvider.orderedStream().toList().get(0);
		} else {
			var parent = applicationContext.getBean(blueskyPropertiesClass, blueskyPropertiesBeanName);
			blueskyModuleProperties = moduleBeanProvider.stream().filter(x-> x.getParent() == parent).findFirst().orElseThrow(() -> new BlueskyException("NOT EXIST TARGET BLUESKY MODULE PROPERTIES"));
		}
		
		return getProperties(blueskyModuleProperties);
	}
	
	private static <T extends BlueskyProperties, U extends BlueskyModuleProperties<T>> T getProperties(U blueskyModuleProperties) {
		var moduleName = getContext().getModuleName();
		if (moduleName == null || blueskyModuleProperties.getModules() == null || !blueskyModuleProperties.getModules().containsKey(moduleName)) {
			return blueskyModuleProperties.getParent();
		}
		return blueskyModuleProperties.getModules().get(moduleName);
	}
	
	/**
	 * coreProperties의 경우 가장 자주 쓰이기 때문에 기본 제공
	 * @return
	 */
	public static CoreProperties getCoreProperties() {
		return getProperties(CoreProperties.class);	
	}
}
