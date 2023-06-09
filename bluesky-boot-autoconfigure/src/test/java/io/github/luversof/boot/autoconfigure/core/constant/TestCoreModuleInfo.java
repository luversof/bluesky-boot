package io.github.luversof.boot.autoconfigure.core.constant;

import java.util.Locale;

import io.github.luversof.boot.autoconfigure.core.CoreProperties.CoreModuleProperties;
import io.github.luversof.boot.autoconfigure.core.CoreProperties.CoreModuleProperties.CoreModulePropertiesBuilder;

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
