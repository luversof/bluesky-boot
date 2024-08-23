package io.github.luversof.boot.web.servlet.i18n;

import java.util.Locale;

import lombok.Data;

@Data
public class LocaleResolveInfo {
	
	private String handlerBeanName;
	
	private Locale requestLocale;
	
	private Locale resolveLocale;

}
