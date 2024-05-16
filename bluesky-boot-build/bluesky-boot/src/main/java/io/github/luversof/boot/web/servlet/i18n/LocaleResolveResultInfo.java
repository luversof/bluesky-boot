package io.github.luversof.boot.web.servlet.i18n;

import java.util.Map;

import org.springframework.context.i18n.LocaleContext;

import lombok.Data;

@Data
public class LocaleResolveResultInfo {
	
	private Map<String, LocaleResolveResult> resultMap;
	
	private LocaleContext localeContext;
	
}
