package io.github.luversof.boot.web.servlet.i18n;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.i18n.LocaleContext;

import lombok.Data;

@Data
public class LocaleResolveInfo {
	
	private List<LocaleResolveResult> resultList = new ArrayList<>();
	
	// TimeZone 을 같이 쓰는 경우도 있는 듯 함 TimeZoneAwareLocaleContext
	private LocaleContext localeContext;
	
}
