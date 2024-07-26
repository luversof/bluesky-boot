package io.github.luversof.boot.web;

import lombok.Data;

@Data
public class LocaleResolveHandlerProperties {

	// 앞서 설정한 locale을 참고할지 여부 RESULT_LIST_ASC/DESC 또는 지정된 Locale을 참고할지?
	private UsePreLocaleResolveInfo usePreLocaleResolveInfo;
	
	/**
	 * 사용할 Locale로 지정 여부
	 */
	private Boolean isRepresentativeLocale;
	
	public enum UsePreLocaleResolveInfo {
		ASC,
		DESC,
		SUPPLIER
	}
}
