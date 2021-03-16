package io.github.luversof.boot.autoconfigure.core.constant;

import java.util.Locale;

import io.github.luversof.boot.autoconfigure.core.config.CoreProperties.CoreModuleProperties;
import io.github.luversof.boot.autoconfigure.core.config.CoreProperties.CoreModuleProperties.CoreModulePropertiesBuilder;
import io.github.luversof.boot.autoconfigure.core.constant.CoreModuleInfo;

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
