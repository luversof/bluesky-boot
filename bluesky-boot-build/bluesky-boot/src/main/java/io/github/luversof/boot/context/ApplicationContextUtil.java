package io.github.luversof.boot.context;

import org.springframework.context.ApplicationContext;

/**
 * A utility used to access ApplicationContext from a static method
 * Use it by adding applicationContext through
 * BlueskyApplicationContextInitializer
 */
public final class ApplicationContextUtil {

	/**
	 * Handling utility class constructors
	 */
	private ApplicationContextUtil() {
	}

	private static ApplicationContext applicationContext;

	public static void setApplicationContext(ApplicationContext applicationContext) {
		ApplicationContextUtil.applicationContext = applicationContext;
	}

	/**
	 * Obtaining applicationContext
	 * 
	 * @return ApplicationContext
	 */
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}
}
