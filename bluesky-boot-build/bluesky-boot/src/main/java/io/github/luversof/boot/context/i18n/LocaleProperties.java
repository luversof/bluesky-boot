package io.github.luversof.boot.context.i18n;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.luversof.boot.core.BlueskyProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 기본적인 Locale 관련 설정을 관리
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = "bluesky-boot.locale")
public class LocaleProperties implements BlueskyProperties {
	
	private static final long serialVersionUID = 1L;
	
	public static final String DEFAULT_BEAN_NAME = "localeProperties";
	public static final String EXTERNAL_LOCALE_BEAN_NAME = "externalLocaleProperties";
	
	private String beanName;

	/**
	 * 사용 가능 로케일 목록,
	 * 배열의 첫번째 로케일이 defaultLocale로 처리되며 만약 설정하지 않은 경우 Locale.default()를 사용
	 */
	@Builder.Default
	private List<Locale> enableLocaleList = new ArrayList<>();
	
	public Locale getDefaultLocale() {
		if (enableLocaleList == null || enableLocaleList.isEmpty()) {
			return Locale.getDefault();
		}
		return enableLocaleList.get(0);
	}

}
