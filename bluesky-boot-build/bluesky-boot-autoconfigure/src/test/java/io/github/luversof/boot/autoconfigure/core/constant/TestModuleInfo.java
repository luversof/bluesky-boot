package io.github.luversof.boot.autoconfigure.core.constant;

import java.util.Locale;

import io.github.luversof.boot.context.i18n.LocaleProperties;
import io.github.luversof.boot.context.i18n.LocaleProperties.LocalePropertiesBuilder;
import io.github.luversof.boot.core.ModuleInfo;
import io.github.luversof.boot.core.CoreProperties;
import io.github.luversof.boot.core.CoreProperties.CorePropertiesBuilder;

public enum TestModuleInfo implements ModuleInfo {
	TEST {
		@Override
		public LocalePropertiesBuilder getLocaleBuilder() {
			return getLocaleBuilder().defaultLocale(Locale.KOREA);
		}
	},
	TEST2 {
		@Override
		public LocalePropertiesBuilder getLocaleBuilder() {
			return getLocaleBuilder().defaultLocale(Locale.CANADA);
		}
	}
	;
	
	public CorePropertiesBuilder getCoreBuilder() {
		return CoreProperties.builder();
	}
	
	public LocalePropertiesBuilder getLocaleBuilder() {
		return LocaleProperties.builder();
	}

}
