package io.github.luversof.boot.web.servlet.i18n;

import java.util.Locale;

import lombok.Data;

/**
 *	An object containing information related to locale resolve processing.
 */
@Data
public class LocaleResolveInfo {
	
	private String handlerBeanName;
	
	private Locale requestLocale;
	
	private Locale resolveLocale;

}
