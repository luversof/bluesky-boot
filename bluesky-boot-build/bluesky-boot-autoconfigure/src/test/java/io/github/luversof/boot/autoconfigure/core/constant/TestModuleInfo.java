package io.github.luversof.boot.autoconfigure.core.constant;

import java.util.Locale;

import io.github.luversof.boot.context.i18n.LocaleProperties.LocalePropertiesBuilder;
import io.github.luversof.boot.core.ModuleInfo;

public enum TestModuleInfo implements ModuleInfo {
	TEST {
		@Override
		public LocalePropertiesBuilder getLocalePropertiesBuilder() {
			return getLocalePropertiesBuilder().defaultLocale(Locale.KOREA);
		}
	},
	TEST2 {
		@Override
		public LocalePropertiesBuilder getLocalePropertiesBuilder() {
			return getLocalePropertiesBuilder().defaultLocale(Locale.CANADA);
		}
	}
	;

}
