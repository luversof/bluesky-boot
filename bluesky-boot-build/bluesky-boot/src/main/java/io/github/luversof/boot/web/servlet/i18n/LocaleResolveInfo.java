package io.github.luversof.boot.web.servlet.i18n;

import org.springframework.context.i18n.LocaleContext;

import lombok.Data;

@Data
public class LocaleResolveInfo {
	
	private int order;
	
	// 이거 개선 필요. 하나의 class에 설정을 다양하게 하는 경우가 있음
	// spring bean의 이름을 그대로 쓸 수 있을까?
	private String handlerBeanName;
	
	private LocaleContext requestLocaleContext;
	
	private LocaleContext resolveLocaleContext;

}
