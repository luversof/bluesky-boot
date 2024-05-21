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
	
//	/**
//	 * 이거 필요 없을 듯
//	 * enableLocaleList의 첫번째를 default로 간주
//	 */
//	private Locale defaultLocale;

	@Builder.Default
	private List<Locale> enableLocaleList = new ArrayList<>();

}
