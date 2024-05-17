package io.github.luversof.boot.web.servlet.i18n;

import org.springframework.context.i18n.LocaleContext;

import lombok.Data;

@Data
public class LocaleResolveResult {
	
	private int order;
	private String handlerClassName;
	
	private LocaleContext requestLocaleContext;
	
	private LocaleContext resolveLocaleContext;

}
