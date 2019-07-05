package net.luversof.boot.autoconfigure.core.util;

import org.springframework.context.ApplicationContext;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ApplicationContextUtil {
	
	@Setter
	private static ApplicationContext applicationContext;
	
	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}
}
