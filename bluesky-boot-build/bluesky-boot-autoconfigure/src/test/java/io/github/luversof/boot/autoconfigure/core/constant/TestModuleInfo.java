package io.github.luversof.boot.autoconfigure.core.constant;

import java.util.List;
import java.util.Locale;

import io.github.luversof.boot.context.i18n.LocaleProperties;
import io.github.luversof.boot.context.i18n.LocaleProperties.LocalePropertiesBuilder;
import io.github.luversof.boot.core.ModuleInfo;

public enum TestModuleInfo implements ModuleInfo {
	TEST {
		@Override
		public LocalePropertiesBuilder getLocalePropertiesBuilder() {
			return LocaleProperties.builder().enableLocaleList(List.of(Locale.KOREA));
		}
	},
	TEST2 {
		@Override
		public LocalePropertiesBuilder getLocalePropertiesBuilder() {
			return LocaleProperties.builder().enableLocaleList(List.of(Locale.CANADA));
		}
	}
	;

}
