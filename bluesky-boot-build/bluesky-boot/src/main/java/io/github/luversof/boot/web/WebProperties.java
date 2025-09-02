package io.github.luversof.boot.web;

import java.util.List;
import java.util.function.BiConsumer;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.PropertyMapper;

import io.github.luversof.boot.context.BlueskyBootContextHolder;
import io.github.luversof.boot.core.AbstractBlueskyProperties;
import io.github.luversof.boot.core.BlueskyPropertiesBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ConfigurationProperties(prefix = WebProperties.PREFIX)
@EqualsAndHashCode(callSuper = true)
public class WebProperties extends AbstractBlueskyProperties<WebProperties, WebProperties.WebPropertiesBuilder> {
	
	private static final long serialVersionUID = 1L;
	
	public static final String PREFIX = "bluesky-boot.web";

	/**
	 * Whether to check unsupported browsers
	 */
	private Boolean checkNotSupportedBrowser = true;
	
	/**
	 * Unsupported browser check pattern list
	 */
	private List<String> notSupportedBrowserRegPatternList = List.of(".*(MSIE [5-9]).*");
	
	/**
	 * Registering an exception list address pattern when checking for unsupported browser
	 */
	private List<String> notSupportedBrowserExcludePathPatternList = List.of("/css/**", "/html/**", "/js/**", "/img/**", "/message/**", "/favicon.ico", "/monitor/**", "/support/**", "/error/**");

	@Override
	protected BiConsumer<WebProperties, WebPropertiesBuilder> getPropertyMapperConsumer() {
		return (properties, builder) -> {
			var propertyMapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
			propertyMapper.from(properties::getCheckNotSupportedBrowser).to(builder::checkNotSupportedBrowser);
			propertyMapper.from(properties::getNotSupportedBrowserRegPatternList).to(builder::notSupportedBrowserRegPatternList);
			propertyMapper.from(properties::getNotSupportedBrowserExcludePathPatternList).to(builder::notSupportedBrowserExcludePathPatternList);			
		};
	}
	
	@Override
	protected WebPropertiesBuilder getBuilder() {
		var blueskyBootContext = BlueskyBootContextHolder.getContext();
		var parentModuleInfo = blueskyBootContext.getParentModuleInfo();
		return parentModuleInfo == null ? WebProperties.builder() : parentModuleInfo.getWebPropertiesBuilder();
	}

	public static WebPropertiesBuilder builder() {
		return new WebPropertiesBuilder();
	}

	public static class WebPropertiesBuilder implements BlueskyPropertiesBuilder<WebProperties> {
		
		private Boolean checkNotSupportedBrowser = true;
		
		private List<String> notSupportedBrowserRegPatternList = List.of(".*(MSIE [5-9]).*");
		
		private List<String> notSupportedBrowserExcludePathPatternList = List.of("/css/**", "/html/**", "/js/**", "/img/**", "/message/**", "/favicon.ico", "/monitor/**", "/support/**", "/error/**");
		
		public WebPropertiesBuilder checkNotSupportedBrowser(Boolean checkNotSupportedBrowser) {
			this.checkNotSupportedBrowser = checkNotSupportedBrowser;
			return this;
		}
		
		public WebPropertiesBuilder notSupportedBrowserRegPatternList(List<String> notSupportedBrowserRegPatternList) {
			this.notSupportedBrowserRegPatternList = notSupportedBrowserRegPatternList;
			return this;
		}
		
		public WebPropertiesBuilder notSupportedBrowserExcludePathPatternList(List<String> notSupportedBrowserExcludePathPatternList) {
			this.notSupportedBrowserExcludePathPatternList = notSupportedBrowserExcludePathPatternList;
			return this;
		}
		
		@Override
		public WebProperties build() {
			return new WebProperties(
				checkNotSupportedBrowser,
				notSupportedBrowserRegPatternList,
				notSupportedBrowserExcludePathPatternList
			);
		}
	}
}
