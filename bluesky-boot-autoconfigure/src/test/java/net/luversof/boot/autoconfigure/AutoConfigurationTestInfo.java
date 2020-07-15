package net.luversof.boot.autoconfigure;

import java.util.HashSet;

import net.luversof.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import net.luversof.boot.autoconfigure.core.config.CoreAutoConfiguration;
import net.luversof.boot.autoconfigure.mongo.config.MongoAutoConfiguration;

public class AutoConfigurationTestInfo {

	public static String[] BASE_PROPERTY = new String[] { "net-profile=opdev" };
	
	public static Class<?>[] CORE_USER_CONFIGURATION = new Class<?>[] { CoreAutoConfiguration.class };
	
	public static Class<?>[] CORE_MESSAGESOURCE_CONFIGURATION = new Class<?>[] { org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration.class };
	
	public static Class<?>[] CORE_MESSAGESOURCE_USER_CONFIGURATION = addClassAll(CORE_USER_CONFIGURATION, MessageSourceAutoConfiguration.class);
	
	public static Class<?>[] MONGO_CONFIGURATION = new Class<?>[] { org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration.class };
	
	public static Class<?>[] MONGO_USER_CONFIGURATION = new Class<?>[] { MongoAutoConfiguration.class };

	
	private static String[] addAll(String[] target, String...strings) {
		var set = new HashSet<>();
		
		for (var str : target) {
			set.add(str);
		}
		
		for (var str : strings) {
			set.add(str);
		}
		
		return set.toArray(new String[set.size()]);
	}
	
	private static Class<?>[] addClassAll(Class<?>[] target, Class<?>... classes) {
		var set = new HashSet<>();
		
		for (var clazz : target) {
			set.add(clazz);
		}

		for (var clazz : classes) {
			set.add(clazz);
		}
		return set.toArray(new Class<?>[set.size()]);
	}
	
	private static Class<?>[] addClassAll(Class<?>[] target, Class<?>[] target2, Class<?>... classes) {
		return addClassAll(target, addClassAll(target2, classes));
	}

	private static Class<?>[] addClassAll(Class<?>[] target, Class<?>[] target2, Class<?>[] target3, Class<?>... classes) {
		return addClassAll(target, addClassAll(target2, target3, classes));
	}
	
	private static Class<?>[] addClassAll(Class<?>[] target, Class<?>[] target2, Class<?>[] target3, Class<?>[] target4, Class<?>... classes) {
		return addClassAll(target, addClassAll(target2, target3, target4, classes));
	}
	
	private static Class<?>[] addClassAll(Class<?>[] target, Class<?>[] target2, Class<?>[] target3, Class<?>[] target4, Class<?>[] target5, Class<?>... classes) {
		return addClassAll(target, addClassAll(target2, target3, target4, target5, classes));
	}
	
	private static Class<?>[] addClassAll(Class<?>[] target, Class<?>[] target2, Class<?>[] target3, Class<?>[] target4, Class<?>[] target5, Class<?>[] target6, Class<?>... classes) {
		return addClassAll(target, addClassAll(target2, target3, target4, target5, target6, classes));
	}
}