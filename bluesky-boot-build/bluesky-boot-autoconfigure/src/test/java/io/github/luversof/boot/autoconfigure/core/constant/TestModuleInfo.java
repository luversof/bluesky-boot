package io.github.luversof.boot.autoconfigure.core.constant;

import java.util.List;
import java.util.Locale;

import io.github.luversof.boot.context.i18n.LocaleProperties;
import io.github.luversof.boot.core.ModuleInfo;

public enum TestModuleInfo implements ModuleInfo {
  TEST {
    @Override
    public LocaleProperties getLocaleProperties() {
      return locales(Locale.KOREA);
    }
  },
  TEST2 {
    @Override
    public LocaleProperties getLocaleProperties() {
      return locales(Locale.CANADA);
    }
  };

  private static LocaleProperties locales(Locale... locales) {
    var localeProperties = new LocaleProperties();
    localeProperties.setEnabledLocales(List.of(locales));
    return localeProperties;
  }
}
