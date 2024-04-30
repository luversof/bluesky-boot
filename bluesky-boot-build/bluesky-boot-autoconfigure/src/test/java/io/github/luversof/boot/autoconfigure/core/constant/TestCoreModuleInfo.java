package io.github.luversof.boot.autoconfigure.core.constant;

import java.util.Locale;

import io.github.luversof.boot.core.CoreModuleInfo;
import io.github.luversof.boot.core.CoreProperties;
import io.github.luversof.boot.core.CoreProperties.CorePropertiesBuilder;

public enum TestCoreModuleInfo implements CoreModuleInfo {
	TEST {
		@Override
		public CorePropertiesBuilder getBuilder() {
			return CoreProperties.builder().defaultLocale(Locale.KOREA);
		}
	},
	TEST2 {
		@Override
		public CorePropertiesBuilder getBuilder() {
			return CoreProperties.builder().defaultLocale(Locale.CANADA);
		}
	}
	;


}
