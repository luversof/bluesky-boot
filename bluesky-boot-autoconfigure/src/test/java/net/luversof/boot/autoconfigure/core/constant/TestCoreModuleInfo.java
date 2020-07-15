package net.luversof.boot.autoconfigure.core.constant;

import java.util.Locale;

import net.luversof.boot.autoconfigure.core.config.CoreProperties.CoreModuleProperties;
import net.luversof.boot.autoconfigure.core.config.CoreProperties.CoreModuleProperties.CoreModulePropertiesBuilder;

public enum TestCoreModuleInfo implements CoreModuleInfo {
	TEST {
		@Override
		public CoreModulePropertiesBuilder getBuilder() {
			return CoreModuleProperties.builder().defaultLocale(Locale.KOREA);
		}
	},
	TEST2 {
		@Override
		public CoreModulePropertiesBuilder getBuilder() {
			return CoreModuleProperties.builder().defaultLocale(Locale.CANADA);
		}
	}
	;


}
