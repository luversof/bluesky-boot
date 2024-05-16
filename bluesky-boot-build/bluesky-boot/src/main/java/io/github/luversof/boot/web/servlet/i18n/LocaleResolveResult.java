package io.github.luversof.boot.web.servlet.i18n;

import java.util.Locale;

import lombok.Data;

@Data
public class LocaleResolveResult {
	
	private int order;
	
	private Locale requestLocale;
	
	private Locale resolveLocale;

}
